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
2. Run `LocalKinesisProducerApp`
3. Inject data into rabbitMq queue `local_kinesis_consume`.

## Trouble Shooting

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis create-stream --stream-name local_kinesis_consume --shard-count 5 

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis list-streams

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis describe-stream --stream-name local_kinesis_stream

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis put-record --stream-name local_kinesis_stream --data data2 --partition-key 2

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis get-shard-iterator --stream-name local_kinesis_stream --shard-id shardId-000000000000 --shard-iterator-type LATEST

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis get-shard-iterator --stream-name local_kinesis_stream --shard-id shardId-000000000000 --shard-iterator-type TRIM_HORIZON

http_proxy="" https_proxy="" HTTP_PROXY="" HTTPS_PROXY="" AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4568/ kinesis get-records --shard-iterator AAAAAAAAAAFEf9wxcnEDvvnZxYwkWYRru2SelzAYgmJhcmMJsgJMW2iUhdyW9g8yqGI0mbZg/t0TjZbnuCct14hC9JVoRGtKxnUjHV+gnQAG0xlXmjaoSnNb/0fDvDavkXyXMxZR0b6h87YTaL8hrzDLnSEmygw6UVsLSazcKxBSYIYwmbjT/MbgFLaBKzH75RNtRKayGoY=


AWS_ACCESS_KEY_ID=x AWS_SECRET_ACCESS_KEY=y aws --endpoint-url http://localhost:4567/ kinesis put-record --stream-name local_kinesis_stream --data test --partition-key 2
