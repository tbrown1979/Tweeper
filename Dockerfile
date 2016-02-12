FROM hseeberger/scala-sbt

MAINTAINER Taylor Brown

RUN ls

ADD . /opt/app

WORKDIR /opt/app

#RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target

#RUN export SBT_OPTS="-Xms256M -Xmx384M -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops"
#-Xss512k

RUN sbt -mem 128 compile
#-mem 256 compile




RUN ls

RUN ls ./target/universal/stage/bin

EXPOSE 8080
