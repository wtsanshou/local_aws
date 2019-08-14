package ie.wtsanshou.local.aws.local.kinesis.node.stream;

import akka.NotUsed;
import akka.stream.ClosedShape;
import akka.stream.alpakka.amqp.javadsl.AmqpSource;
import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import akka.stream.alpakka.kinesis.javadsl.KinesisSink;
import akka.stream.javadsl.*;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import ie.wtsanshou.local.aws.local.kinesis.node.config.BackOffConfig;
import ie.wtsanshou.local.aws.local.kinesis.node.config.LocalKinesisConfig;
import ie.wtsanshou.local.aws.local.kinesis.node.config.RabbitmqConfig;
import ie.wtsanshou.local.aws.local.kinesis.node.translator.KinesisRecordsTranslator;
import ie.wtsanshou.local.aws.local.kinesis.node.translator.Parser;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class StreamFactory {

    private StreamFactory() {
    }

    @SuppressWarnings("unchecked")
    public static RunnableGraph<NotUsed> streamOf(Source<CommittableReadResult, NotUsed> source, Flow<CommittableReadResult, PutRecordsRequestEntry, NotUsed> flow, Sink<PutRecordsRequestEntry, NotUsed> actorSink) {
        return RunnableGraph.fromGraph(
                GraphDSL.create(
                        actorSink,
                        (builder, sink) -> {
                            builder.from(builder.add(source).out())
                                    .via(builder.add(flow))
                                    .to(sink);
                            return ClosedShape.getInstance();
                        }));
    }

    public static Source<CommittableReadResult, NotUsed> sourceOf(RabbitmqConfig rabbitmqConfig, BackOffConfig backOffConfig) {
        return RestartSource.onFailuresWithBackoff(
                Duration.ofSeconds(backOffConfig.MIN_BACKOFF_SECONDS),
                Duration.ofSeconds(backOffConfig.MAX_BACKOFF_SECONDS),
                backOffConfig.RANDOM_FACTOR,
                () -> {
                    log.info("Connecting to rabbit settings={}, backoff={}", rabbitmqConfig, backOffConfig);
                    return AmqpSource.committableSource(rabbitmqConfig.SETTING, rabbitmqConfig.BUFFER_SIZE);
                });
    }

    public static Flow<CommittableReadResult, PutRecordsRequestEntry, NotUsed> flow() {
        return Flow.of(CommittableReadResult.class)
                .via(parseFlow())
                .via(translateFlow())
                .via(ackFlow());
    }

    public static Sink<PutRecordsRequestEntry, NotUsed> sinkOf(LocalKinesisConfig localKinesisConfig) {
        return KinesisSink.create(localKinesisConfig.STREAM_NAME, localKinesisConfig.KINESIS_FLOW_SETTINGS, localKinesisConfig.AMAZON_KINESIS_ASYNC);
    }

    private static Flow<CommittableReadResult, Tuple2<CommittableReadResult, String>, NotUsed> parseFlow() {
        return Flow.of(CommittableReadResult.class).map(Parser::decode);
    }

    private static Flow<Tuple2<CommittableReadResult, String>, Tuple2<CommittableReadResult, PutRecordsRequestEntry>, NotUsed> translateFlow() {
        return Flow.<Tuple2<CommittableReadResult, String>>create().map(KinesisRecordsTranslator::transferToRecord);
    }

    private static Flow<Tuple2<CommittableReadResult, PutRecordsRequestEntry>, PutRecordsRequestEntry, NotUsed> ackFlow() {
        return Flow.<Tuple2<CommittableReadResult, PutRecordsRequestEntry>>create().map(pair -> {
            pair._1.ack();
            log.info("One message={} received get confirmation", pair._1);
            return pair._2;
        });
    }

}
