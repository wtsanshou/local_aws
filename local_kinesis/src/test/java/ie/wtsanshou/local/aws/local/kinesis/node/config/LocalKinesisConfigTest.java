package ie.wtsanshou.local.aws.local.kinesis.node.config;

import akka.stream.alpakka.kinesis.KinesisFlowSettings;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalKinesisConfigTest {

    private final LocalKinesisConfig localKinesisConfig = ConfigFactory.INSTANCE.getLocalKinesisConfig();

    @Test
    @DisplayName("Local Kinesis config should be loaded correctly")
    public void testLocalKinesisConfigProperties() {
        assertEquals("testHost", localKinesisConfig.HOST);
        assertEquals(8888, localKinesisConfig.PORT);
        assertEquals("eu-west-1", localKinesisConfig.REGION);
        assertEquals("testStream", localKinesisConfig.STREAM_NAME);

        assertEquals(500, localKinesisConfig.MAX_RECORDS_PER_REQUEST);
        assertEquals(1_000, localKinesisConfig.MAX_RECORDS_PER_SECOND);
        assertEquals(1_000_000, localKinesisConfig.MAX_BYTES_PER_SECOND);
        assertEquals(10, localKinesisConfig.MAX_RETRIES);
        assertEquals(3, localKinesisConfig.RETRY_INITIAL_TIMEOUT);
        assertEquals(10, localKinesisConfig.PARALLELISM);


        assertEquals(1, localKinesisConfig.REFRESH_INTERVAL);
        // the maximum number of records that GetRecords can return
        assertEquals(500, localKinesisConfig.MAX_RECORDS_PER_GET_RECORDS);
    }

    @Test
    @DisplayName("Local Kinesis localAwsCredentials should be loaded correctly")
    public void testLocalAwsCredentials() {
        final AWSCredentialsProvider aws_credentials_provider = localKinesisConfig.AWS_CREDENTIALS_PROVIDER;
        final AWSCredentials credentials = aws_credentials_provider.getCredentials();
        assertEquals("testAccessKey", credentials.getAWSAccessKeyId());
        assertEquals("testSecretKey", credentials.getAWSSecretKey());
    }

    @Test
    @DisplayName("Local Kinesis KinesisFlowSettings should be loaded correctly")
    public void testKinesisFlowSettings() {
        final KinesisFlowSettings kinesis_flow_settings = localKinesisConfig.KINESIS_FLOW_SETTINGS;
        assertEquals(500, kinesis_flow_settings.maxBatchSize());
        assertEquals(1_000, kinesis_flow_settings.maxRecordsPerSecond());
        assertEquals(1_000_000, kinesis_flow_settings.maxBytesPerSecond());
        assertEquals(10, kinesis_flow_settings.maxRetries());
        assertEquals(FiniteDuration.create(3, TimeUnit.SECONDS), kinesis_flow_settings.retryInitialTimeout());
        assertEquals(10, kinesis_flow_settings.parallelism());
        assertEquals("Exponential", kinesis_flow_settings.backoffStrategy().toString());
    }

}
