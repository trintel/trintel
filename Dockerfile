FROM openjdk:11-jre-slim
ENV TZ=Europe/Berlin

COPY build/ /build/

EXPOSE 8080

ENTRYPOINT ["java","-jar","/build/libs/trintel-0.0.1-SNAPSHOT.jar"]

