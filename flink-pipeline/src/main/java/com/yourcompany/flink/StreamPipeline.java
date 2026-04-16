package com.yourcompany.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.yourcompany.flink.dataMapper.InventoryMapper;
import com.yourcompany.flink.dataMapper.OrderItemMapper;
import com.yourcompany.flink.models.InventoryEvent;
import com.yourcompany.flink.models.InventoryLedgerEvent;
import com.yourcompany.flink.models.OrderBucketEvent;
import com.yourcompany.flink.models.OrderItemEvent;
import com.yourcompany.flink.models.ValidatedEvent;
import com.yourcompany.flink.sink.InventoryLedgerSink;
import com.yourcompany.flink.sink.PaymentSink;
import com.yourcompany.flink.sink.RejectedOrdersSink;
import com.yourcompany.flink.source.InventorySource;
import com.yourcompany.flink.source.OrderSource;
import com.yourcompany.flink.source.PaymentSource;
import com.yourcompany.flink.source.ShipmentSource;
import com.yourcompany.flink.stateFunctions.InventoryOrderValidationState;
import com.yourcompany.flink.statics.OrderValidationStatus;
import com.yourcompany.flink.task.BucketOrders;
import com.yourcompany.flink.task.ValidateStream;
import com.yourcompany.flink.utils.JsonMapper;

public class StreamPipeline {

        public static void main(String[] args) throws Exception {

                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

                DataStream<InventoryEvent> inventoryStream = env.fromSource(InventorySource.create(),
                                WatermarkStrategy.noWatermarks(), "Inventory Source").map(new InventoryMapper())
                                .name("Inventory Event Mapper");
                DataStream<OrderItemEvent> orderItemStream = env
                                .fromSource(OrderSource.create(), WatermarkStrategy.noWatermarks(), "Order Source")
                                .flatMap(new OrderItemMapper())
                                .name("Order Item Event Mapper");
                DataStream<OrderItemEvent> paymentStream = env
                                .fromSource(PaymentSource.create(), WatermarkStrategy.noWatermarks(), "Payment Source")
                                .flatMap(new OrderItemMapper())
                                .name("Payment Item Event Mapper");
                DataStream<OrderItemEvent> shipmentStream = env
                                .fromSource(ShipmentSource.create(), WatermarkStrategy.noWatermarks(),
                                                "Shipment Source")
                                .flatMap(new OrderItemMapper())
                                .name("Shipment Item Event Mapper");
                DataStream<OrderItemEvent> unifiedStream = orderItemStream.union(paymentStream).union(shipmentStream);
                SingleOutputStreamOperator<ValidatedEvent> validatedStream = ValidateStream.validateStream(
                                inventoryStream,
                                unifiedStream);

                DataStream<InventoryLedgerEvent> ledgerEvents = validatedStream
                                .getSideOutput(InventoryOrderValidationState.LEDGER_OUTPUT);

                ledgerEvents.map(event -> JsonMapper.toJson(event)).sinkTo(InventoryLedgerSink.create());

                DataStream<OrderBucketEvent> bucketedOrders = BucketOrders.bucketOrder(validatedStream);

                bucketedOrders.filter(event -> OrderValidationStatus.REJECTED == event.getStatus())
                                .map(event -> JsonMapper.toJson(event))
                                .sinkTo(RejectedOrdersSink.create());

                bucketedOrders.filter(event -> OrderValidationStatus.ACCEPTED == event.getStatus())
                                .map(event -> JsonMapper.toJson(event))
                                .sinkTo(PaymentSink.create());

                env.execute("Flink Streaming Pipeline");
        }
}