package ie.wtsanshou.local.aws.local.kinesis;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import ie.wtsanshou.local.aws.local.kinesis.node.config.ConfigFactory;
import ie.wtsanshou.local.aws.local.kinesis.node.stream.StreamFactory;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeoutException;

@Slf4j
public class LocalKinesisProducerApp {

    public static void main(String[] args) {

        // XXX: Important to have this line to make local kinesis work
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");

        final ActorSystem system = ActorSystem.create("localKinesisProducerApp");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source source = StreamFactory.sourceOf(ConfigFactory.INSTANCE.getRabbitmqConfig(),
                ConfigFactory.INSTANCE.getBackOffConfig());
        final Flow flow = StreamFactory.flow();
        final Sink sink = StreamFactory.sinkOf(ConfigFactory.INSTANCE.getLocalKinesisConfig());

        final RunnableGraph stream = StreamFactory.streamOf(source, flow, sink);
        stream.run(materializer);

        try {
            Await.ready(system.whenTerminated(), Duration.Inf());
        } catch (InterruptedException | TimeoutException e) {
            log.error("Actor System process terminated unexpectedly", e);
        }

    }
}
