FROM openjdk:11-jre-slim
WORKDIR ./home/trintel

COPY /target/app.jar app.jar
COPY /target/resources/ build/resources/

EXPOSE 8080

ENTRYPOINT ["java","-jar","/home/trintel/app.jar"]

