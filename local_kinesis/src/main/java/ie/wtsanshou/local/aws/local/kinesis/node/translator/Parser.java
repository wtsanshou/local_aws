package ie.wtsanshou.local.aws.local.kinesis.node.translator;

import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import ie.wtsanshou.local.aws.local.kinesis.node.exception.ParserException;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Parser {

    private Parser() {
    }

    public static Tuple2<CommittableReadResult, String> decode(CommittableReadResult crr) throws ParserException {
        try {
            return Tuple.of(crr, crr.message().bytes().utf8String());
        } catch (Exception e) {
            throw new ParserException(e, crr);
        }
    }
}
