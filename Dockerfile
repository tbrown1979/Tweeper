FROM williamyeh/sbt

MAINTAINER Taylor Brown

WORKDIR /

ADD . /opt/app

WORKDIR /opt/app

RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target

RUN sbt assembly

RUN mv /opt/app/target/scala-2.10/app-assembly-0.1-SNAPSHOT.jar .

RUN mv /opt/app/app-assembly-0.1-SNAPSHOT.jar /opt/app/app.jar

#ADD ./target/scala-2.10/tweeper-assembly-0.1-SNAPSHOT.jar /opt/app/app.jar

EXPOSE 8080
