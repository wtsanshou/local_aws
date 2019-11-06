import actor.PersistentAccount;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import domain.DepositToAccount;
import domain.WithdrawFromAccount;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class PersistAccountApp {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("accountApp");
        ClusterShardingSettings settings = ClusterShardingSettings.create(system)
                .withPassivateIdleAfter(Duration.ofSeconds(10));

        ActorRef persistCurrentAccountRegion =
                ClusterSharding.get(system)
                        .start("PersistCurrentAccount", PersistentAccount.props(5), settings, PersistentAccount.shardRegionMessageExtractor);

        persistCurrentAccountRegion.tell(new DepositToAccount(100000127, 123.5), ActorRef.noSender());
        persistCurrentAccountRegion.tell(new DepositToAccount(100000125, 123.5), ActorRef.noSender());

        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000127, 12.5), ActorRef.noSender());
        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000125, 23.5), ActorRef.noSender());

        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000127, 12.5), ActorRef.noSender());
        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000125, 23.5), ActorRef.noSender());

        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000127, 12.5), ActorRef.noSender());
        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000125, 23.5), ActorRef.noSender());

        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000127, 12.5), ActorRef.noSender());
        persistCurrentAccountRegion.tell(new WithdrawFromAccount(100000125, 23.5), ActorRef.noSender());
    }
}
