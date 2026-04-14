package com.yourcompany.flink.stateFunctions;

import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import com.yourcompany.flink.models.InventoryEvent;
import com.yourcompany.flink.models.OrderItemEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.statics.OrderValidationStatus;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import com.yourcompany.flink.statics.EventStatus;

public class InventoryOrderValidationState
        extends KeyedCoProcessFunction<String, InventoryEvent, OrderItemEvent, ValidatedEvent> {
    private ValueState<Integer> inventoryState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> inventory_state_desc = new ValueStateDescriptor<>("inventory_qty", Integer.class);
        this.inventoryState = getRuntimeContext().getState(inventory_state_desc);
    }

    @Override
    public void processElement1(InventoryEvent event, Context ctx, Collector<ValidatedEvent> out)
            throws Exception {
        inventoryState.update(event.getQuantity());
        OrderValidationStatus status = "last".equals(event.getSnapshot()) ? OrderValidationStatus.SNAPSHOT_COMPLETED
                : OrderValidationStatus.STATE_UPDATED;
        out.collect(ValidatedEvent.inventory(event, status));
    }

    @Override
    public void processElement2(OrderItemEvent event, Context ctx, Collector<ValidatedEvent> out) throws Exception {
        Integer available_qty = inventoryState.value();
        int quantity = event.getQuantity();
        OrderValidationStatus status;
        if (EventStatus.CREATE_ORDER.getValue().equals(event.getEventType())) {
            if (available_qty == null) {
                status = OrderValidationStatus.INVALID;
            } else if (available_qty.intValue() >= quantity) {
                status = OrderValidationStatus.ACCEPTED;
                inventoryState.update(available_qty.intValue() - quantity);
            } else
                status = OrderValidationStatus.REJECTED;
        } else if (EventStatus.FAILED_PAYMENT.getValue().equals(event.getEventType()) |
                EventStatus.FAILED_SHIPMENT.getValue().equals(event.getEventType())) {
            if (available_qty == null) {
                status = OrderValidationStatus.INVALID;
            } else {
                status = OrderValidationStatus.RELEASED;
                inventoryState.update(available_qty.intValue() + quantity);
            }
        } else {
            status = OrderValidationStatus.INVALID;
        }
        out.collect(ValidatedEvent.order(event, status));
    }
}
