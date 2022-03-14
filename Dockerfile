FROM openjdk:8-jdk-alpine

COPY /build/libs/trintel-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]

