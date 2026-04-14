package com.yourcompany.flink.statics;

public enum EventSource {
    ORDERS("orders"),
    PAYMENTS("payments"),
    SHIPMENTS("shipments");

    final String value;

    EventSource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventSource fromValue(String raw) {
        for (EventSource s : values()) {
            if (s.value.equals(raw)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + raw);
    }
}
