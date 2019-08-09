package ie.wtsanshou.local.aws.local.kinesis.node.stream;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

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

}
