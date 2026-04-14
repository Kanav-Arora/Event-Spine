package com.yourcompany.flink.statics;

public enum EventStatus {
    CREATE_ORDER("create-order", EventSource.ORDERS.value),
    REJECT_ORDER("reject-order", EventSource.ORDERS.value),
    COMPLETED_ORDER("completed-order", EventSource.ORDERS.value),

    SUCCESS_PAYMENT("success-payment", EventSource.PAYMENTS.value),
    FAILED_PAYMENT("failed-payment", EventSource.PAYMENTS.value),

    SUCCESS_SHIPMENT("success-shipment", EventSource.SHIPMENTS.value),
    FAILED_SHIPMENT("failed-shipment", EventSource.SHIPMENTS.value);

    final String value;
    final String domain;

    EventStatus(String value, String domain) {
        this.value = value;
        this.domain = domain;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public static EventStatus fromValue(String raw) {
        for (EventStatus s : values()) {
            if (s.value.equals(raw)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + raw);
    }
}
