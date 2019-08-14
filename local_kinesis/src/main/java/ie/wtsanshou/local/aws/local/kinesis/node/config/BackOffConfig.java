package ie.wtsanshou.local.aws.local.kinesis.node.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class BackOffConfig {

    public final int MIN_BACKOFF_SECONDS;
    public final int MAX_BACKOFF_SECONDS;
    public final double RANDOM_FACTOR;

    BackOffConfig() {
        final Config CONFIG = ConfigFactory.load().getConfig("backoff");
        MIN_BACKOFF_SECONDS = CONFIG.getInt("minbackoff-seconds");
        MAX_BACKOFF_SECONDS = CONFIG.getInt("maxbackoff-seconds");
        RANDOM_FACTOR = CONFIG.getDouble("random-factor");
    }
}
