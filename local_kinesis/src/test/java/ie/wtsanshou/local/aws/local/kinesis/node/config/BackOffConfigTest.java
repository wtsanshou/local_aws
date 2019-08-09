package ie.wtsanshou.local.aws.local.kinesis.node.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackOffConfigTest {

    private final BackOffConfig backoffConfig = ConfigFactory.INSTANCE.getBackOffConfig();

    @Test
    @DisplayName("Back Off config should be loaded correctly")
    void testBackOffConfigProperties() {
        assertEquals(3, backoffConfig.MIN_BACKOFF_SECONDS);
        assertEquals(60, backoffConfig.MAX_BACKOFF_SECONDS);
        assertEquals(0.2, backoffConfig.RANDOM_FACTOR);
    }
}
