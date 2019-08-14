package ie.wtsanshou.local.aws.local.kinesis.node.translator;

import akka.stream.alpakka.amqp.ReadResult;
import akka.stream.alpakka.amqp.javadsl.CommittableReadResult;
import akka.util.ByteString;
import ie.wtsanshou.local.aws.local.kinesis.node.exception.ParserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ParserTest {

    private CommittableReadResult committableReadResult = Mockito.mock(CommittableReadResult.class);


    @Test
    @DisplayName("Parser should decode Rabbitmq ReadResult to string")
    public void parserDecodeReadResultTest() {
        ByteString testByte = ByteString.fromArray("test".getBytes());
        final var readResult = ReadResult.create(testByte, null, null);
        when(committableReadResult.message()).thenReturn(readResult);

        assertEquals("test", Parser.decode(committableReadResult)._2);
    }

    @Test
    @DisplayName("Should return error in case of decode null message")
    void testParserError() {
        when(committableReadResult.message()).thenReturn(ReadResult.create(null, null, null));

        ParserException thrown = assertThrows(ParserException.class,
                () -> Parser.decode(committableReadResult));
        assertTrue(thrown.getCause() instanceof NullPointerException);
        assertEquals(committableReadResult, thrown.getMsg());
    }

}
