FROM amazoncorretto:8
MAINTAINER Frinx Support <support@frinx.io>
# apt-get according to https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/
RUN yum update -y && yum install -y less zip unzip net-tools curl && rm -rf /var/lib/apt/lists/*
WORKDIR /opt
ADD target/cloudapi-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/
ADD hikari.properties /opt/
ADD run.sh /opt/
EXPOSE 50051
ENTRYPOINT ["/opt/run.sh"]