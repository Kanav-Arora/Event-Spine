package com.yourcompany.flink.models;

import java.io.Serializable;

public class OrderItemEvent implements Serializable {
    private String source;
    private String aggregateType;
    private String eventType;
    private String order_id;
    private String inventory_id;
    private int quantity;
    private int total_items;

    public OrderItemEvent() {
    }

    public OrderItemEvent(String source, String eventType, String aggregateType, String order_id, String inventory_id,
            int quantity,
            int total_items) {
        this.source = source;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.order_id = order_id;
        this.inventory_id = inventory_id;
        this.quantity = quantity;
        this.total_items = total_items;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getInventoryId() {
        return inventory_id;
    }

    public void setInventoryId(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalItems() {
        return total_items;
    }

    public void setTotalItems(int total_items) {
        this.total_items = total_items;
    }

    @Override
    public String toString() {
        return "OrderItemEvent{" +
                "source='" + source + '\'' +
                "aggregateType='" + aggregateType + '\'' +
                "eventType='" + eventType + '\'' +
                ", orderId='" + order_id + '\'' +
                ", inventoryId='" + inventory_id + '\'' +
                ", quantity=" + quantity +
                ", totalItems=" + total_items +
                '}';
    }

}
