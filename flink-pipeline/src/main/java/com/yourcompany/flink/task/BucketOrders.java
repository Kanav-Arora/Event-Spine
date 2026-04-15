package com.yourcompany.flink.task;

import org.apache.flink.streaming.api.datastream.DataStream;

import com.yourcompany.flink.models.OrderBucketEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.stateFunctions.OrderBucketState;
import com.yourcompany.flink.statics.EventStatus;

public class BucketOrders {
    public static DataStream<OrderBucketEvent> bucketOrder(DataStream<ValidatedEvent> validatedStream) {
        return validatedStream.filter(r -> EventStatus.CREATE_ORDER.getValue().equals(r.getEventType()))
                .keyBy(ValidatedEvent::getOrderId)
                .process(new OrderBucketState());
    }
}
