FROM amazon/dynamodb-local:latest
USER root

ENV AWS_DEFAULT_REGION us-west-2
ENV AWS_ACCESS_KEY_ID x
ENV AWS_SECRET_ACCESS_KEY y
RUN curl -k -O https://bootstrap.pypa.io/get-pip.py
RUN python get-pip.py --user
RUN python ~/.local/bin/pip install awscli --upgrade --user
RUN yum install nc -y
COPY setup-dynamodb.sh /usr/bin/setup-dynamodb
ENTRYPOINT ["/usr/bin/env"]
CMD ["bash", "/usr/bin/setup-dynamodb"]
