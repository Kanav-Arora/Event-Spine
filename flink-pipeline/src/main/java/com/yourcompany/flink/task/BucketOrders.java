package com.yourcompany.flink.task;

import org.apache.flink.streaming.api.datastream.DataStream;

import com.yourcompany.flink.models.OrderBucketEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.stateFunctions.OrderBucketState;
import com.yourcompany.flink.statics.EventSource;

public class BucketOrders {
    public static DataStream<OrderBucketEvent> bucketOrder(DataStream<ValidatedEvent> validatedStream) {
        return validatedStream.filter(r -> EventSource.ORDERS.getValue().equals(r.getSource()))
                .keyBy(ValidatedEvent::getOrderId)
                .process(new OrderBucketState());
    }
}
