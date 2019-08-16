FROM localstack/localstack

RUN pip install nclib

COPY create-stream.sh /usr/local/bin/create-stream.sh
RUN chmod +x /usr/local/bin/create-stream.sh
COPY my-docker-entrypoint.sh /usr/local/bin/my-docker-entrypoint.sh
RUN chmod +x /usr/local/bin/my-docker-entrypoint.sh

ENTRYPOINT ["my-docker-entrypoint.sh"]

