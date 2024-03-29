package ie.wtsanshou.local.aws.local.kinesis.node.config;

public enum ConfigFactory {

    INSTANCE;

    private RabbitmqConfig rabbitmqConfig;
    private BackOffConfig backOffConfig;
    private LocalKinesisConfig localKinesisConfig;

    public synchronized RabbitmqConfig getRabbitmqConfig() {
        if (rabbitmqConfig == null) {
            rabbitmqConfig = new RabbitmqConfig();
        }
        return rabbitmqConfig;
    }

    public synchronized BackOffConfig getBackOffConfig() {
        if (backOffConfig == null)
            backOffConfig = new BackOffConfig();
        return backOffConfig;
    }

    public LocalKinesisConfig getLocalKinesisConfig() {
        if (localKinesisConfig == null)
            localKinesisConfig = new LocalKinesisConfig();
        return localKinesisConfig;
    }
}
