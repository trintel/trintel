# Stage 1: Build the application
FROM gradle:7.2.0-jdk11 AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle
COPY .git .git

COPY src ./src

RUN ./gradlew --no-daemon -x test build

# Stage 2: Create the final image
FROM openjdk:11-jdk-slim

ENV TZ=Europe/Berlin

COPY --from=builder /app/build/libs/*.jar /app/trintel.jar

COPY --from=builder /app/build/resources/main/static/img/placeholder.jpg /app/build/resources/main/static/img/placeholder.jpg

EXPOSE 8080

EXPOSE 5005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/trintel.jar"]

