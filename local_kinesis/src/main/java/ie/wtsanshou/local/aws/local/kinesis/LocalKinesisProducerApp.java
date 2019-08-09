package ie.wtsanshou.local.aws.local.kinesis;

import akka.Done;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import ie.wtsanshou.local.aws.local.kinesis.node.config.ConfigFactory;
import ie.wtsanshou.local.aws.local.kinesis.node.stream.StreamFactory;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;

@Slf4j
public class LocalKinesisProducerApp {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("localKinesisProducerApp");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source source = StreamFactory.sourceOf(ConfigFactory.INSTANCE.getRabbitmqConfig(),
                ConfigFactory.INSTANCE.getBackOffConfig());
        final Flow flow = StreamFactory.flow();

        final Sink sink = StreamFactory.sinkOf(system.dispatcher());

        final RunnableGraph<Pair<UniqueKillSwitch, CompletionStage<Done>>> stream = StreamFactory.streamOf(source, flow, sink);
        Pair<UniqueKillSwitch, CompletionStage<Done>> switchWithCompletion = stream.run(materializer);

        switchWithCompletion.second().exceptionally(throwable -> {
                    log.error("Encountered Exception that stops stream, terminating the system.", throwable);
                    system.terminate();
                    return Done.done();
                }
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Got Control-C, shutting down by kill switch");
            switchWithCompletion.first().shutdown();
        }
        ));

        try {
            Await.ready(system.whenTerminated(), Duration.Inf());
        } catch (InterruptedException | TimeoutException e) {
            log.error("Actor System process terminated unexpectedly", e);
        }

    }
}
