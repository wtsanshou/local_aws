package ie.wtsanshou.local.aws.local.kinesis.node.translator;

import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.function.Function;

@Slf4j
public class KinesisRecordsTranslator {

    private KinesisRecordsTranslator() {
    }

    public static Tuple2<CommittableReadResult, PutRecordsRequestEntry> transferToRecord(Tuple2<CommittableReadResult, String> message) {
        return message.map(Function.identity(), KinesisRecordsTranslator::transferToRecord);
    }

    private static PutRecordsRequestEntry transferToRecord(String data) {
        log.info("The message={} is transferred to Kinesis record", data);
        final var putRecordsRequestEntry = new PutRecordsRequestEntry();

        putRecordsRequestEntry.setData(ByteBuffer.wrap(data.getBytes()));
        final int partitionKey = data.hashCode();
        putRecordsRequestEntry.setPartitionKey(String.valueOf(partitionKey));
        return putRecordsRequestEntry;

    }
}
