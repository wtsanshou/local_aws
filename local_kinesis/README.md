# Local Kinesis

This project has two applications:

1. KinesisProducer is to consume data from RabbitMQ and produce the data to Kinesis.
2. KinesisConsumer is to consume data from local Kinesis and print the data.

## Overview


```
+----------------+             +---------------+          +----------------+           +---------------+
|                |             |               |          |                |           |               |
|  RabbitMQ      +------------>+KinesisProducer+--------->+    Kinesis     +---------->+KinesisConsumer|
|                |             |               |          |                |           |               |
+----------------+             +---------------+          +----------------+           +---------------+
```

## Setup

1. Run `docker-compose up`
2. Create stream in Kinesis (Will put it in Dockerfile): `http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis create-stream --stream-name local_kinesis_consume --shard-count 5`
3. Run `LocalKinesisProducerApp`
4. Inject data into rabbitMq queue `local_kinesis_consume`.

