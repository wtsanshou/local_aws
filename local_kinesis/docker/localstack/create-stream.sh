#!/bin/sh

echo "starting creating stream----------------------"
while ! nc -z localhost 4568; do sleep 1; done
aws --region $DEFAULT_REGION --endpoint-url http://localhost:4568/ kinesis create-stream --stream-name $KINESIS_STREAM_NAME --shard-count $KINESIS_SHARD_COUNT
echo "finish creating stream ======================="
