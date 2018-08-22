FROM alpine:3.8

WORKDIR /root
RUN apk add --update openjdk8-jre
COPY build/install/core .

CMD ./bin/core
