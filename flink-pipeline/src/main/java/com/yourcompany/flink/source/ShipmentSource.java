package com.yourcompany.flink.source;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import com.yourcompany.flink.deserializer.OrderRecordDeserializer;
import com.yourcompany.flink.models.OrderDeserializedEvent;
import com.yourcompany.flink.config;

public class ShipmentSource {
    public static KafkaSource<OrderDeserializedEvent> create() {
        return KafkaSource.<OrderDeserializedEvent>builder()
                .setBootstrapServers("kafka:29092").setTopics(config.shipment_source_topic)
                .setGroupId(config.group_id).setStartingOffsets(
                        OffsetsInitializer.committedOffsets(
                                OffsetResetStrategy.EARLIEST))
                .setDeserializer(new OrderRecordDeserializer()).build();
    }
}
