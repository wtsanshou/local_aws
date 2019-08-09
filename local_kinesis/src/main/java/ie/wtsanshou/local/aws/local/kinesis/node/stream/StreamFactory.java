package ie.wtsanshou.local.aws.local.kinesis.node.stream;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.ActorAttributes;
import akka.stream.KillSwitches;
import akka.stream.Supervision;
import akka.stream.UniqueKillSwitch;
import akka.stream.alpakka.amqp.javadsl.AmqpSource;
import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import akka.stream.javadsl.*;
import ie.wtsanshou.local.aws.local.kinesis.node.config.BackOffConfig;
import ie.wtsanshou.local.aws.local.kinesis.node.config.RabbitmqConfig;
import ie.wtsanshou.local.aws.local.kinesis.node.exception.ParserException;
import ie.wtsanshou.local.aws.local.kinesis.node.translator.Parser;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.ExecutionContext;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

@Slf4j
public class StreamFactory {

    private StreamFactory() {
    }

    @SuppressWarnings("unchecked")
    public static RunnableGraph<Pair<UniqueKillSwitch, CompletionStage<Done>>> streamOf(Source source, Flow flow, Sink sink) {
        return source
                .via(flow)
                .viaMat(KillSwitches.single(), Keep.right())
                .toMat(sink, Keep.both())
                .withAttributes(ActorAttributes.withSupervisionStrategy(StreamFactory::decider));
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

    public static Flow<CommittableReadResult, Tuple2<CommittableReadResult, String>, NotUsed> flow() {
        return Flow.of(CommittableReadResult.class).map(Parser::decode);
    }

    public static Sink<Tuple2<CommittableReadResult, String>, CompletionStage<Done>> sinkOf(ExecutionContext context) {
        return Sink.foreachParallel(2,
                tuple -> {
                    System.out.println(tuple);
                    tuple._1.ack();
                },
                context);
    }

    private static Supervision.Directive decider(Throwable exception) {
        if (exception instanceof ParserException) {
            final ParserException e = (ParserException) exception;
            e.getMsg().ack();
            final var message = Parser.decode(e.getMsg());
            log.warn("Cannot parse the message={}, exception={}", message, e);
            return Supervision.restart();
        } else {
            log.error("Fatal Error occurred.", exception);
            return Supervision.stop();
        }
    }
}
