package com.yourcompany.flink;

public class config {
    public static final String inventory_source_topic = "inventory.events";
    public static final String orders_source_topic = "orders.events";
    public static final String payment_source_topic = "payments.events";
    public static final String shipment_source_topic = "shipments.events";
    public static final String validated_orders_sink_topic = "validated.orders";
    public static final String rejected_orders_sink_topic = "rejected.orders";
    public static final String payments_sink_topic = "request.payments";
    public static final String group_id = "inventory-order-job";
}
