FROM openjdk:11
LABEL maintainer="teo@bran.tech"

ENV _JAVA_OPTIONS="-Xms64m"
ENV CONFIG_FILE="application-config.yaml"

COPY target/kryptonite-*.jar /usr/src/kryptonite/app.jar

VOLUME /etc/kryptonite/
WORKDIR /usr/src/kryptonite

CMD java -jar app.jar --spring.config.location=file:/etc/kryptonite/${CONFIG_FILE}
