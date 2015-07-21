FROM nightscape/docker-sbt

MAINTAINER Taylor Brown

RUN ls

ADD . /opt/app

WORKDIR /opt/app

#RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target

RUN export SBT_OPTS="-Xss512k -Xms128M -Xmx384M -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops"

RUN sbt stage

RUN ls

RUN ls ./target/universal/stage/bin

EXPOSE 8080
