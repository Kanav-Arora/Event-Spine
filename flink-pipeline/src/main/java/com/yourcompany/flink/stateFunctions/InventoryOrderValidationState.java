package com.yourcompany.flink.stateFunctions;

import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.common.protocol.types.Field.Bool;

import com.yourcompany.flink.models.InventoryEvent;
import com.yourcompany.flink.models.InventoryLedgerEvent;
import com.yourcompany.flink.models.OrderItemEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.statics.OrderValidationStatus;

import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import com.yourcompany.flink.statics.EventStatus;
import com.yourcompany.flink.statics.InventoryLedgerType;

public class InventoryOrderValidationState
        extends KeyedCoProcessFunction<String, InventoryEvent, OrderItemEvent, ValidatedEvent> {
    private ValueState<Integer> inventoryState;
    // private MapState<String, Boolean> seenEventState;

    public static final OutputTag<InventoryLedgerEvent> LEDGER_OUTPUT = new OutputTag<InventoryLedgerEvent>(
            "ledger-event") {
    };

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Integer> inventory_state_desc = new ValueStateDescriptor<>("inventory_qty", Integer.class);
        // MapStateDescriptor<String, Boolean> seen_event_state_desc = new
        // MapStateDescriptor<>("seen_event_state",
        // String.class, Boolean.class);
        this.inventoryState = getRuntimeContext().getState(inventory_state_desc);
        // this.seenEventState = getRuntimeContext().getMapState(seen_event_state_desc);
    }

    @Override
    public void processElement1(InventoryEvent event, Context ctx, Collector<ValidatedEvent> out)
            throws Exception {
        inventoryState.update(event.getQuantity());
        InventoryLedgerEvent ledgerEvent = new InventoryLedgerEvent(event, InventoryLedgerType.SNAPSHOT);
        ctx.output(LEDGER_OUTPUT, ledgerEvent);
    }

    @Override
    public void processElement2(OrderItemEvent event, Context ctx, Collector<ValidatedEvent> out) throws Exception {
        Integer available_qty = inventoryState.value();
        int quantity = event.getQuantity();
        OrderValidationStatus status;
        // create-order events
        if (EventStatus.CREATE_ORDER.getValue().equals(event.getEventType())) {
            if (available_qty == null) {
                status = OrderValidationStatus.INVALID;
            } else if (available_qty.intValue() >= quantity) {
                status = OrderValidationStatus.ACCEPTED;
                inventoryState.update(available_qty.intValue() - quantity);
                ctx.output(LEDGER_OUTPUT, new InventoryLedgerEvent(event, InventoryLedgerType.RESERVED));
            } else
                status = OrderValidationStatus.REJECTED;
        }
        // failed-payment or failed-shipment events
        else if (EventStatus.FAILED_PAYMENT.getValue().equals(event.getEventType()) |
                EventStatus.FAILED_SHIPMENT.getValue().equals(event.getEventType())) {
            if (available_qty == null) {
                status = OrderValidationStatus.INVALID;
            } else {
                status = OrderValidationStatus.ACCEPTED;
                inventoryState.update(available_qty.intValue() + quantity);
                ctx.output(LEDGER_OUTPUT, new InventoryLedgerEvent(event, InventoryLedgerType.RELEASED));
            }
        }
        // complete-order
        else if (EventStatus.COMPLETED_ORDER.getValue().equals(event.getEventType())) {
            status = OrderValidationStatus.ACCEPTED;
            ctx.output(LEDGER_OUTPUT, new InventoryLedgerEvent(event, InventoryLedgerType.COMMITTED));
        } else {
            status = OrderValidationStatus.INVALID;
        }
        out.collect(ValidatedEvent.order(event, status));
    }
}
