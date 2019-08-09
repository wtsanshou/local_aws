package ie.wtsanshou.local.aws.local.kinesis.node.exception;

import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import lombok.Getter;

public class ParserException extends RuntimeException {
    @Getter
    private final CommittableReadResult msg;

    public ParserException(Throwable cause, CommittableReadResult msg) {
        super(cause);
        this.msg = msg;
    }
}
