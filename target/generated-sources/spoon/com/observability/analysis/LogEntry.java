package com.observability.analysis;
import java.util.Map;
public class LogEntry {
    private final String user;

    private final String action;

    private final String type;// READ or WRITE


    private final Map<String, Object> data;

    private LogEntry(Builder builder) {
        this.user = builder.user;
        this.action = builder.action;
        this.type = builder.type;
        this.data = builder.data;
    }

    public String getUser() {
        return user;
    }

    public String getAction() {
        return action;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public static class Builder {
        private String user;

        private String action;

        private String type;

        private Map<String, Object> data;

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder data(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public LogEntry build() {
            return new LogEntry(this);
        }
    }
}