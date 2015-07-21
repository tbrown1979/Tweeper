FROM nightscape/docker-sbt

MAINTAINER Taylor Brown

RUN ls

ADD . /opt/app

WORKDIR /opt/app

#RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target
RUN export SBT_OPTS="-Xms128M -Xmx256M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=512M -XX:+UseConcMarkSweepGC"

RUN sbt stage

RUN ls

RUN ls ./target/universal/stage/bin

EXPOSE 8080
