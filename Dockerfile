# a Dockerfile for creating the SURFER build environment
FROM phusion/baseimage:0.9.16

RUN apt-get update && apt-get -yy install openjdk-7-jdk ant wget unzip && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
RUN wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/javafx/1.3.1-b05/javafx_sdk-1_3_1-b05a-linux-i586.zip" && unzip javafx_sdk-1_3_1-b05a-linux-i586.zip -d /opt/ && rm javafx_sdk-1_3_1-b05a-linux-i586.zip

# this assumes that the SURFER git repository is mounted into /SURFER
WORKDIR /SURFER

CMD ["ant","-Dplatforms.default_fx_platform.fxhome=/opt/javafx-sdk1.3", "compile"]
