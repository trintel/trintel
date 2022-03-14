FROM openjdk:8-jdk-alpine
WORKDIR .

COPY /target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]

