package actor;

import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.cluster.sharding.ShardRegion.MessageExtractor;
import akka.persistence.*;
import domain.DepositToAccount;
import domain.WithdrawFromAccount;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistentAccount extends AbstractPersistentActor {

    public static MessageExtractor shardRegionMessageExtractor = new MessageExtractor() {

        @Override
        public String entityId(Object message) {
            if (message instanceof DepositToAccount)
                return ((DepositToAccount) message).getAccountNo().toString();
            if (message instanceof WithdrawFromAccount)
                return ((WithdrawFromAccount) message).getAccountNo().toString();
            else return null;
        }

        @Override
        public Object entityMessage(Object message) {
            return message;
        }

        @Override
        public String shardId(Object message) {
            int numberOfShards = 100;
            if (message instanceof DepositToAccount) {
                long id = ((DepositToAccount) message).getAccountNo().hashCode();
                return String.valueOf(id % numberOfShards);
            } else if (message instanceof WithdrawFromAccount) {
                long id = ((WithdrawFromAccount) message).getAccountNo().hashCode();
                return String.valueOf(id % numberOfShards);
            } else if (message instanceof ShardRegion.StartEntity) {
                String id = ((ShardRegion.StartEntity) message).entityId();
                return String.valueOf(Long.valueOf(id) % numberOfShards);
            } else {
                return null;
            }
        }
    };
    private double balance;
    private long snapshotInterval;

    private PersistentAccount(long snapshotInterval) {
        this.snapshotInterval = snapshotInterval;
        this.balance = 0;
    }

    public static Props props(long snapshotInterval) {
        return Props.create(PersistentAccount.class, () -> new PersistentAccount(snapshotInterval));
    }

    @Override
    public String persistenceId() {
        return "PersistentCurrentAccount-" + getId();
    }

    private String getId() {
        return self().path().name();
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(DepositToAccount.class, this::applyDeposit)
                .match(WithdrawFromAccount.class, this::applyWithdraw)
                .match(SnapshotOffer.class, s -> balance = (double) s.snapshot())
                .match(RecoveryCompleted.class,  c -> log.info("Persistent Current Account : " + getId() + " - Balance = " + balance))
                .build();
    }

    @Override
    public Receive createReceive() { //thread-safe
        return receiveBuilder()
                .match(DepositToAccount.class,
                        d -> {
                            log.info("Persistent Current Account : " + getId() + " - Deposit made of " + d.getAmount());
                            persist(d, this::applyDeposit);  //d is the data will be saved, applyDeposit is the event will be execute
                            //persistAsync();
                        })
                .match(WithdrawFromAccount.class,
                        d -> {
                            log.info("Persistent Current Account : " + getId() + " - Withdraw made of " + d.getAmount());
                            persist(d, this::applyWithdraw);
                            //persistAsync();
                        })
                .match(ReceiveTimeout.class,
                        r -> {
                            log.info("Persistent Current Account : " + getId() + " - ReceiveTimeout");
                            getContext().getParent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
                        })
                .match(PoisonPill.class, p -> getContext().stop(getSelf()))
                .match(SaveSnapshotSuccess.class, s -> deleteSnapshots(SnapshotSelectionCriteria.create(snapshotSequenceNr() - 1L, Long.MAX_VALUE)))
                .match(SaveSnapshotFailure.class, s -> log.error("Error saving snapshot sequenceNumber={}", s.metadata().sequenceNr()))
                .build();
    }

    private void applyWithdraw(WithdrawFromAccount withdrawFromAccount) {
        balance -= withdrawFromAccount.getAmount();
        snapshot();
        if (!recoveryRunning()) log.info("Persistent Account withdraw : " + getId() + " - Balance = " + balance);
    }

    private void applyDeposit(DepositToAccount d) {
        balance += d.getAmount();
        snapshot();
        if (!recoveryRunning()) log.info("Persistent Account deposit : " + getId() + " - Balance = " + balance);
    }

    private void snapshot() {
        if (lastSequenceNr() % snapshotInterval == 0 && lastSequenceNr() != 0) {
            saveSnapshot(balance);
            log.info("Saved a snapshot balance={}", balance);
        }
    }
}

