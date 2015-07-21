FROM nightscape/docker-sbt

MAINTAINER Taylor Brown

RUN ls

ADD . /opt/app

WORKDIR /opt/app

#RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target

RUN export SBT_OPTS="-Xss512k -Xms32M -Xmx128M -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops"

RUN sbt -mem 384 compile

RUN ls

RUN ls ./target/universal/stage/bin

EXPOSE 8080
