FROM openjdk:openjdk11
WORKDIR .

COPY /target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]

