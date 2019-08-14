package ie.wtsanshou.local.aws.local.kinesis.node.config;

import akka.japi.Pair;
import akka.stream.alpakka.amqp.AmqpCredentials;
import akka.stream.alpakka.amqp.AmqpDetailsConnectionProvider;
import akka.stream.alpakka.amqp.NamedQueueSourceSettings;
import akka.stream.alpakka.amqp.QueueDeclaration;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RabbitmqConfig {

    public final String QUEUE_NAME;
    public final String USERNAME;
    public final String PASSWORD;
    public final List<Pair<String, Object>> ADDRESS;
    public final String VIRTUAL_HOST;
    public final int BUFFER_SIZE;
    public final NamedQueueSourceSettings SETTING;

    RabbitmqConfig() {
        final var CONFIG = ConfigFactory.load().getConfig("inbound.rabbitmq");
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

        final var amqpDetailsConnectionProvider = AmqpDetailsConnectionProvider
                .create("", 0)
                .withHostsAndPorts(ADDRESS)
                .withCredentials(AmqpCredentials.create(USERNAME, PASSWORD))
                .withVirtualHost(VIRTUAL_HOST)
                .withAutomaticRecoveryEnabled(false)
                .withTopologyRecoveryEnabled(false);

        SETTING = NamedQueueSourceSettings
                .create(amqpDetailsConnectionProvider, QUEUE_NAME)
                .withDeclaration(QueueDeclaration.create(QUEUE_NAME).withDurable(false))
                .withAckRequired(true);
    }
}
