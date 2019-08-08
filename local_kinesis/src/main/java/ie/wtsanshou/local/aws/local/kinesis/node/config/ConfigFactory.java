package ie.wtsanshou.local.aws.local.kinesis.node.config;

public enum ConfigFactory {

    INSTANCE;

    private InboundConfig inboundConfig;

    public synchronized InboundConfig getInboundConfig() {
        if (inboundConfig == null) {
            inboundConfig = new InboundConfig();
        }
        return inboundConfig;
    }
}
