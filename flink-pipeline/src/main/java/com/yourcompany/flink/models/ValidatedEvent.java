package com.yourcompany.flink.models;

import java.io.Serializable;

import com.yourcompany.flink.statics.OrderValidationStatus;

public class ValidatedEvent implements Serializable {
    private String source;
    private String eventType;
    private String aggregateType;
    private String order_id;
    private String inventory_id;
    private int quantity;
    private int total_items;
    private String updated_at;
    private String snapshot;
    private OrderValidationStatus status;

    public ValidatedEvent() {
    }

    public static ValidatedEvent inventory(InventoryEvent event, OrderValidationStatus status) {
        ValidatedEvent r = new ValidatedEvent();
        r.source = event.getSource();
        r.inventory_id = event.getInventoryId();
        r.quantity = event.getQuantity();
        r.updated_at = event.getUpdatedAt();
        r.snapshot = event.getSnapshot();
        r.status = status;
        return r;
    }

    public static ValidatedEvent order(OrderItemEvent event, OrderValidationStatus status) {
        ValidatedEvent r = new ValidatedEvent();
        r.source = event.getSource();
        r.eventType = event.getEventType();
        r.aggregateType = event.getAggregateType();
        r.order_id = event.getOrderId();
        r.inventory_id = event.getInventoryId();
        r.quantity = event.getQuantity();
        r.total_items = event.getTotalItems();
        r.status = status;
        return r;
    }

    @Override
    public String toString() {
        return String.format(
                "{\"source\":\"%s\",\"eventType\":\"%s\",\"aggregateType\":\"%s\",\"order_id\":\"%s\",\"inventory_id\":\"%s\",\"quantity\":%d,\"total_items\":%d,\"updated_at\":\"%s\",\"snapshot\":\"%s\",\"status\":\"%s\"}",
                source, eventType, aggregateType, order_id, inventory_id,
                quantity, total_items, updated_at, snapshot,
                status != null ? status.name() : null);
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

    public String getOrderId() {
        return order_id;
    }

    public String getInventoryId() {
        return inventory_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalItems() {
        return total_items;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public OrderValidationStatus getStatus() {
        return status;
    }
}
