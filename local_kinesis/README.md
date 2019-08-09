# Local Kinesis

This project has two applications:

1. KinesisProducer is to consumer data from RabbitMQ and produce the data to Kinesis.
2. KinesisConsumer is to consumer data from local Kinesis and print the data.

## Overview


```
+----------------+             +---------------+          +----------------+           +---------------+
|                |             |               |          |                |           |               |
|  RabbitMQ      +------------>+KinesisProducer+--------->+    Kinesis     +---------->+KinesisConsumer|
|                |             |               |          |                |           |               |
+----------------+             +---------------+          +----------------+           +---------------+
```


