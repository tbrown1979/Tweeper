FROM williamyeh/scala

MAINTAINER Taylor Brown

WORKDIR /


ADD . /opt/app

WORKDIR /opt/app

RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target 

RUN sbt assembly

EXPOSE 8080
