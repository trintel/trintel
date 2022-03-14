FROM openjdk:8-jdk-alpine
WORKDIR .

COPY /target/app.jar app.jar

EXPOSE 8080

RUN apt-get update && \
    apt-get install -y openjdk-11-jre-headless && \
    apt-get clean;

ENTRYPOINT ["java","-jar","/app.jar"]

