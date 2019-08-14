package ie.wtsanshou.local.aws.local.kinesis.node.config;

import akka.stream.alpakka.kinesis.KinesisFlowSettings;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;

public class LocalKinesisConfig {

    public final AmazonKinesisAsync AMAZON_KINESIS_ASYNC;
    public final KinesisFlowSettings KINESIS_FLOW_SETTINGS;
    public final AWSCredentialsProvider AWS_CREDENTIALS_PROVIDER;
    public final int MAX_RECORDS_PER_GET_RECORDS;
    public final int REFRESH_INTERVAL;
    public final int PARALLELISM;
    public final int RETRY_INITIAL_TIMEOUT;
    public final int MAX_RETRIES;
    public final int MAX_BYTES_PER_SECOND;
    public final int MAX_RECORDS_PER_SECOND;
    public final int MAX_RECORDS_PER_REQUEST;
    public final String REGION;
    public final int PORT;
    public final String HOST;
    public final String STREAM_NAME;

    LocalKinesisConfig() {
        final var CONFIG = ConfigFactory.load().getConfig("outbound.kinesis");
        HOST = CONFIG.getString("host");
        PORT = CONFIG.getInt("port");
        REGION = CONFIG.getString("region");
        STREAM_NAME = CONFIG.getString("stream_name");

        MAX_RECORDS_PER_REQUEST = CONFIG.getInt("flow.max_records_per_request");
        MAX_RECORDS_PER_SECOND = CONFIG.getInt("flow.max_records_per_second");
        MAX_BYTES_PER_SECOND = CONFIG.getInt("flow.max_bytes_per_second");
        MAX_RETRIES = CONFIG.getInt("flow.max_retries");
        RETRY_INITIAL_TIMEOUT = CONFIG.getInt("flow.retry_initial_timeout");
        PARALLELISM = CONFIG.getInt("flow.parallelism");

        REFRESH_INTERVAL = CONFIG.getInt("shard.refresh_interval");
        MAX_RECORDS_PER_GET_RECORDS = CONFIG.getInt("shard.max_records_per_get_records");

        final var AWS_ACCESS_KEY = CONFIG.getString("credential.access_key");
        final var AWS_SECRET_KEY = CONFIG.getString("credential.secret_key");

        AWS_CREDENTIALS_PROVIDER = new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY));

        KINESIS_FLOW_SETTINGS = getKinesisFlowSettings();

        AMAZON_KINESIS_ASYNC = getAmazonKinesisAsync();
    }

    private KinesisFlowSettings getKinesisFlowSettings() {
        return KinesisFlowSettings.create()
                .withParallelism(PARALLELISM) // shards * (MAX_RECORDS_PER_SHARD_PER_SECOND / MAX_RECORDS_PER_REQUEST)
                .withMaxBatchSize(MAX_RECORDS_PER_REQUEST) // MAX_RECORDS_PER_REQUEST
                .withMaxRecordsPerSecond(MAX_RECORDS_PER_SECOND) // shards * MAX_RECORDS_PER_SHARD_PER_SECOND
                .withMaxBytesPerSecond(MAX_BYTES_PER_SECOND) // shards * MAX_BYTES_PER_SHARD_PER_SECOND

                // case Exponential => retryInitialTimeout * scala.math.pow(2, result.attempt - 1).toInt
                // case Linear => retryInitialTimeout * result.attempt
                .withBackoffStrategyExponential()
                .withRetryInitialTimeout(Duration.ofSeconds(RETRY_INITIAL_TIMEOUT))
                .withMaxRetries(MAX_RETRIES); // Max time of retries
    }

    private AmazonKinesisAsync getAmazonKinesisAsync() {
        final AmazonKinesisAsyncClientBuilder clientBuilder = AmazonKinesisAsyncClientBuilder.standard();
        final String localKinesisEndpoint = "http://" + HOST + ":" + PORT;
        final AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(localKinesisEndpoint, REGION);
        clientBuilder.withEndpointConfiguration(endpointConfiguration);

        clientBuilder.setCredentials(AWS_CREDENTIALS_PROVIDER);

        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProxyHost("localhost");
        clientBuilder.setClientConfiguration(clientConfiguration);

        return clientBuilder.build();
    }
}
