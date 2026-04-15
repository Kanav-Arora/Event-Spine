package com.yourcompany.flink.models;

import java.io.Serializable;
import java.util.UUID;
import com.yourcompany.flink.statics.InventoryLedgerType;

public class InventoryLedgerEvent implements Serializable {
    private String event_id;
    private String source;
    private String inventory_id;
    private String order_id;
    private int quantity;
    private String updated_at;
    private String snapshot;
    private InventoryLedgerType status;

    public InventoryLedgerEvent(InventoryEvent event, InventoryLedgerType ledgerType) {
        this.event_id = UUID.randomUUID().toString();
        this.source = event.getSource();
        this.inventory_id = event.getInventoryId();
        this.quantity = event.getQuantity();
        this.updated_at = event.getUpdatedAt();
        this.snapshot = event.getSnapshot();
        this.status = ledgerType;
        this.order_id = null;
    }

    public InventoryLedgerEvent(OrderItemEvent event, InventoryLedgerType ledgerType) {
        this.event_id = UUID.randomUUID().toString();
        this.source = event.getSource();
        this.inventory_id = event.getInventoryId();
        this.order_id = event.getOrderId();
        this.quantity = event.getQuantity();
        this.updated_at = null;
        this.snapshot = null;
        this.status = ledgerType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InventoryLedgerEvent{");
        sb.append("event_id='").append(event_id).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", inventory_id='").append(inventory_id).append('\'');
        if (order_id != null) {
            sb.append(", order_id='").append(order_id).append('\'');
        }
        sb.append(", quantity=").append(quantity);
        sb.append(", updated_at='").append(updated_at).append('\'');
        if (snapshot != null) {
            sb.append(", snapshot='").append(snapshot).append('\'');
        }
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    public String getEventId() {
        return event_id;
    }

    public String getOrderId() {
        return order_id;
    }

    public String getSource() {
        return source;
    }

    public String getInventoryId() {
        return inventory_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public InventoryLedgerType getStatus() {
        return status;
    }
}
