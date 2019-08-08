package ie.wtsanshou.local.aws.local.kinesis.node.config;

import akka.japi.Pair;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.stream.Collectors;

public class InboundConfig {

    public final String QUEUE_NAME;
    public final String USERNAME;
    public final String PASSWORD ;
    public final List<Pair<String, Object>> ADDRESS;
    public final String VIRTUAL_HOST;
    public final int BUFFER_SIZE;

    InboundConfig() {
        final Config CONFIG = ConfigFactory.load().getConfig("inbound.rabbitmq");
        QUEUE_NAME = CONFIG.getString("queue");
        USERNAME = CONFIG.getString("username");
        PASSWORD = CONFIG.getString("password");

        ADDRESS = CONFIG.getList("addresses").stream()
                        .map(q -> q.unwrapped().toString().split(":"))
                        .filter(a -> a.length == 2)
                        .filter(a -> a[1].matches("\\d+"))
                        .map(a -> Pair.create(a[0], (Object) Integer.valueOf(a[1])))
                        .collect(Collectors.toList());

        VIRTUAL_HOST = CONFIG.getString("virtual-host");
        BUFFER_SIZE = CONFIG.getInt("buffer-size");
    }
}
