#!/bin.bash

java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb &

while ! nc -z localhost 8000; do sleep 1; done

# create message jornal table
~/.local/bin/aws dynamodb create-table --table-name message_journal \
--attribute-definitions AttributeName=par,AttributeType=S AttributeName=num,AttributeType=N \
--key-schema AttributeName=par,KeyType=HASH AttributeName=num,KeyType=RANGE \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--endpoint-url http://localhost:8000

# create snapchot table
~/.local/bin/aws dynamodb create-table --table-name message_snapshot \
--attribute-definitions AttributeName=par,AttributeType=S AttributeName=seq,AttributeType=N AttributeName=ts,AttributeType=N \
--key-schema AttributeName=par,KeyType=HASH AttributeName=seq,KeyType=RANGE \
--local-secondary-indexes IndexName=ts-idx,KeySchema=["{AttributeName=par,KeyType=HASH}","{AttributeName=ts,KeyType=RANGE}"],Projection="{ProjectionType=ALL}" \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--endpoint-url http://localhost:8000

while true; do sleep 5; done
