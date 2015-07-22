FROM hseeberger/scala-sbt

MAINTAINER Taylor Brown

RUN ls

ADD . /opt/app

WORKDIR /opt/app

RUN sbt -mem 128 stage

RUN ls

RUN ls ./target/universal/stage/bin

EXPOSE 8080
