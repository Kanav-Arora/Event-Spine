package com.yourcompany.flink.dataMapper;

import org.apache.flink.util.Collector;
import org.apache.flink.api.common.functions.FlatMapFunction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.flink.models.OrderDeserializedEvent;
import com.yourcompany.flink.models.OrderItemEvent;

public class OrderItemMapper implements FlatMapFunction<OrderDeserializedEvent, OrderItemEvent> {
    public static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void flatMap(OrderDeserializedEvent deserialized_event, Collector<OrderItemEvent> out) throws Exception {

        JsonNode root = mapper.readTree(deserialized_event.getPayload());

        JsonNode payload = mapper.readTree(root.get("payload").asText());

        String orderId = payload.get("order_id").asText();
        JsonNode orderItems = payload.get("order_items");

        int totalItems = orderItems.size();

        for (JsonNode item : orderItems) {

            OrderItemEvent event = new OrderItemEvent(
                    deserialized_event.getAggregateType(),
                    deserialized_event.getEventType(),
                    deserialized_event.getAggregateType(),
                    orderId,
                    item.get("inventory_id").asText(),
                    item.get("quantity").asInt(),
                    totalItems);

            out.collect(event);
        }
    }

}
