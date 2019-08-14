package ie.wtsanshou.local.aws.local.kinesis.node.stream;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.amqp.ReadResult;
import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class StreamFactoryTest {

    private static ActorSystem system;
    private static Materializer materializer;
    private static CommittableReadResult committableReadResult;


    @BeforeAll
    static void setup() {
        system = ActorSystem.create("localKinesisProducerTest");
        materializer = ActorMaterializer.create(system);
        committableReadResult = Mockito.mock(CommittableReadResult.class);
    }

    @AfterAll
    static void tearDown() {
        system.terminate();
    }

    @Test
    @DisplayName("Stream flow should translate committableReadResult to Tuple with String message")
    public void testFlow() throws InterruptedException, ExecutionException, TimeoutException {
        ByteString testByte = ByteString.fromArray("test".getBytes());
        final var readResult = ReadResult.create(testByte, null, null);
        when(committableReadResult.message()).thenReturn(readResult);
        final Flow<CommittableReadResult, PutRecordsRequestEntry, NotUsed> flow = StreamFactory.flow();

        final CompletionStage<PutRecordsRequestEntry> future =
                Source.from(Collections.singletonList(committableReadResult))
                        .via(flow)
                        .runWith(Sink.head(), materializer);
        final var result = future.toCompletableFuture().get(1, TimeUnit.SECONDS);

        assertEquals(String.valueOf("test".hashCode()), result.getPartitionKey());
        assertEquals("test", new String(result.getData().array()));
    }

//    @Test
//    @DisplayName("Stream Sink should translate committableReadResult to Tuple with String message")
//    public void testSink() {
//        final Tuple2<CommittableReadResult, String> testInput = Tuple.of(committableReadResult, "test");
//
//        final Sink<Tuple2<CommittableReadResult, String>, CompletionStage<Done>> sink = StreamFactory.sinkOf();
//        final TestKit probe = new TestKit(system);
//
//        final CompletionStage<Done> future =
//                Source.from(Collections.singletonList(testInput))
//                .runWith(sink, materializer);
//        akka.pattern.Patterns.pipe(future, system.dispatcher()).to(probe.getRef());
//        probe.expectMsgClass(Duration.ofSeconds(1), done().getClass());
//    }

}
