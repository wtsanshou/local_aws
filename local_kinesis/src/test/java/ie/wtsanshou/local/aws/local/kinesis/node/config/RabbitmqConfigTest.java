package ie.wtsanshou.local.aws.local.kinesis.node.config;

import akka.japi.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RabbitmqConfigTest {

    private final RabbitmqConfig rabbitmqConfig = ConfigFactory.INSTANCE.getRabbitmqConfig();

    @Test
    @DisplayName("Rabbitmq config should be loaded correctly")
    void testRabbitmqConfigProperties() {
        assertEquals(rabbitmqConfig.QUEUE_NAME, "testQueueName");
        assertEquals(rabbitmqConfig.USERNAME, "testUsername");
        assertEquals(rabbitmqConfig.PASSWORD, "testPassword");
        assertEquals(rabbitmqConfig.ADDRESS, List.of(Pair.create("testHost5000", 5000), Pair.create("testHost5001", 5001)));
        assertEquals(rabbitmqConfig.VIRTUAL_HOST, "testVirtualHost");
        assertEquals(rabbitmqConfig.BUFFER_SIZE, 20);
    }

}
