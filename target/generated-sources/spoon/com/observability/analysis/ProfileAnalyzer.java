package com.observability.analysis;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ProfileAnalyzer {
    // Regex to capture JSON part of the log line
    // Assumes format: ... - { "user": ... }
    private static final Pattern JSON_PATTERN = Pattern.compile(".*(\\{.*\\}).*");

    // Simple regex to extract values from JSON (avoiding deps)
    private static final Pattern USER_REGEX = Pattern.compile("\"user\"\\s*:\\s*\"([^\"]+)\"");

    private static final Pattern ACTION_REGEX = Pattern.compile("\"action\"\\s*:\\s*\"([^\"]+)\"");

    private static final Pattern TYPE_REGEX = Pattern.compile("\"type\"\\s*:\\s*\"([^\"]+)\"");

    private static final Pattern PRICE_REGEX = Pattern.compile("\"price\"\\s*:\\s*([0-9.]+)");

    public static void main(String[] args) {
        String logFile = "logs/simulation.log";
        if (args.length > 0)
            logFile = args[0];

        Map<String, UserProfile> profiles = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                LogEntry entry = parseLine(line);
                if (entry != null) {
                    profiles.putIfAbsent(entry.getUser(), new UserProfile(entry.getUser()));
                    profiles.get(entry.getUser()).addEntry(entry);
                }
            } 
        } catch (Exception e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        System.out.println("=== USER PROFILES ===");
        profiles.values().forEach(System.out::println);
    }

    private static LogEntry parseLine(String line) {
        Matcher jsonMatcher = JSON_PATTERN.matcher(line);
        if (!jsonMatcher.find())
            return null;
        // Not a JSON log line

        String json = jsonMatcher.group(1);
        String user = extract(USER_REGEX, json);
        String action = extract(ACTION_REGEX, json);
        String type = extract(TYPE_REGEX, json);
        if (user == null)
            return null;

        Map<String, Object> data = new HashMap<>();
        String priceStr = extract(PRICE_REGEX, json);
        if (priceStr != null) {
            data.put("price", Double.parseDouble(priceStr));
        }
        return new LogEntry.Builder().user(user).action(action).type(type).data(data).build();
    }

    private static String extract(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}