import logging
from opentelemetry.sdk._logs import LoggerProvider, LoggingHandler
from opentelemetry.sdk._logs.export import BatchLogRecordProcessor
from opentelemetry.exporter.otlp.proto.http._log_exporter import OTLPLogExporter
from opentelemetry.sdk.resources import Resource

resource = Resource.create({
    "service.name": "event-emitter",
})

logger_provider = LoggerProvider(resource=resource)

exporter = OTLPLogExporter(
    endpoint="http://otel-collector:4318/v1/logs"
)

logger_provider.add_log_record_processor(
    BatchLogRecordProcessor(exporter,schedule_delay_millis=1000,)
)

handler = LoggingHandler(level=logging.INFO, logger_provider=logger_provider)

loggerWrapper = logging.getLogger("eventspine")
loggerWrapper.addHandler(handler)
loggerWrapper.setLevel(logging.INFO)
loggerWrapper.propagate = False

logging.basicConfig(level=logging.INFO)