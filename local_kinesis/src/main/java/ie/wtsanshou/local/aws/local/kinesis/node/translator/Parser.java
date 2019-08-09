package ie.wtsanshou.local.aws.local.kinesis.node.translator;

import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import ie.wtsanshou.local.aws.local.kinesis.node.exception.ParserException;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Parser {
    public static Tuple2<CommittableReadResult, String> decode(CommittableReadResult rr) throws ParserException {
        try {
            return Tuple.of(rr, rr.message().bytes().utf8String());
        } catch (Exception e) {
            throw new ParserException(e, rr);
        }
    }
}
