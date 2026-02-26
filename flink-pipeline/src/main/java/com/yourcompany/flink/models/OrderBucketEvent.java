package com.yourcompany.flink.models;

import com.yourcompany.flink.statics.OrderValidationStatus;
import java.time.Instant;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBucketEvent {
    private String source;
    private String eventType;
    private String aggregateType;
    @JsonProperty("order_id")
    private String order_id;
    @JsonProperty("total_items")
    private int total_items;
    @JsonProperty("order_items")
    private Map<String, Integer> order_items;
    private OrderValidationStatus status;
    private String timestamp;

    public OrderBucketEvent() {
    }

    public OrderBucketEvent(ValidatedEvent event, OrderValidationStatus status, Map<String, Integer> orderItems) {
        this.source = event.getSource();
        this.eventType = event.getEventType();
        this.aggregateType = event.getAggregateType();
        this.order_id = event.getOrderId();
        this.total_items = event.getTotalItems();
        this.status = status;
        this.timestamp = Instant.now().toString();
        this.order_items = orderItems;
    }

    public String getSource() {
        return source;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public Map<String, Integer> getOrderItems() {
        return order_items;
    }

    public String getOrderId() {
        return order_id;
    }

    public int getTotalItems() {
        return total_items;
    }

    public OrderValidationStatus getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
