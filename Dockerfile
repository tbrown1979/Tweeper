FROM java:8

MAINTAINER Taylor Brown

WORKDIR /

# USER root

#ADD . /opt/app

ADD /target/scala-2.10/tweeper-assembly-0.1-SNAPSHOT.jar /opt/app/server.jar

WORKDIR /opt/app

#ENTRYPOINT /opt/app/target/universal/stage/bin/straw-poll-app

EXPOSE 8080

# FROM flurdy/activator:1.3.2

# MAINTAINER Taylor Brown

# ENV APPDIR /opt/app

# RUN mkdir -p /etc/opt/app

# ADD . /opt/app

# WORKDIR /opt/app

# RUN rm -rf /opt/app/target /opt/app/project/project /opt/app/project/target 

# RUN /usr/local/bin/activator stage

# WORKDIR /opt/app

#ENTRYPOINT /opt/app/target/universal/stage/bin/straw-poll-app

#EXPOSE 9000
