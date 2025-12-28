package com.observability.analysis;
public class UserProfile {
    private final String userId;

    private int readCount = 0;

    private int writeCount = 0;

    private double maxPriceSeen = 0.0;

    public UserProfile(String userId) {
        this.userId = userId;
    }

    public void addEntry(LogEntry entry) {
        if ("READ".equals(entry.getType())) {
            readCount++;
        } else if ("WRITE".equals(entry.getType())) {
            writeCount++;
        }
        if (entry.getData().containsKey("price")) {
            try {
                // Handle Double or Integer
                double price = Double.parseDouble(entry.getData().get("price").toString());
                if (price > maxPriceSeen) {
                    maxPriceSeen = price;
                }
            } catch (Exception e) {
                // Ignore parse error
            }
        }
    }

    public String determineProfileType() {
        if (maxPriceSeen > 1000) {
            return "HIGH_SPENDER";
        }
        if (writeCount > readCount) {
            return "WRITER";
        }
        return "READER";
    }

    @Override
    public String toString() {
        return String.format("User: %s | Profile: %s | Reads: %d | Writes: %d | MaxPrice: %.2f", userId, determineProfileType(), readCount, writeCount, maxPriceSeen);
    }
}