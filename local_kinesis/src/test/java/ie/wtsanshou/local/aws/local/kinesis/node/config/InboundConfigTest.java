package ie.wtsanshou.local.aws.local.kinesis.node.config;

import akka.japi.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InboundConfigTest {

    private final InboundConfig inboundConfig = ConfigFactory.INSTANCE.getInboundConfig();

    @Test
    @DisplayName("Inbound config should be loaded correctly")
    void testConfigProperties() {
        assertEquals(inboundConfig.QUEUE_NAME, "testQueueName");
        assertEquals(inboundConfig.USERNAME, "testUsername");
        assertEquals(inboundConfig.PASSWORD, "testPassword");
        assertEquals(inboundConfig.ADDRESS, List.of(Pair.create("testHost5000", 5000), Pair.create("testHost5001", 5001)));
        assertEquals(inboundConfig.VIRTUAL_HOST, "testVirtualHost");
        assertEquals(inboundConfig.BUFFER_SIZE, 20);
    }

}
