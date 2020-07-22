FROM openjdk:11-jdk-slim AS build

WORKDIR /app/

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src/

RUN ./mvnw package

RUN adduser --system --home /var/cache/bootapp --shell /sbin/nologin bootapp;


FROM adoptopenjdk/openjdk11:alpine-jre

COPY --from=build /etc/passwd /etc/shadow /etc/
ARG DEPENDENCY=/app/target
COPY --from=build ${DEPENDENCY}/*.jar /app.jar

USER bootapp
ENV _JAVA_OPTIONS "-XX:MaxRAMPercentage=90 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true"
ENTRYPOINT ["java","-jar","/app.jar"]
