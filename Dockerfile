# Stage 1: Build the application
FROM gradle:7.2.0-jdk11 AS builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle ./gradle

COPY src ./src

RUN ./gradlew --no-daemon -x test build

# Stage 2: Create the final image
FROM openjdk:11-jre-slim

ENV TZ=Europe/Berlin

COPY --from=builder /app/build/libs/trintel-0.0.1-SNAPSHOT.jar /app/trintel.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/trintel.jar"]


# FROM openjdk:11-jre-slim
# ENV TZ=Europe/Berlin

# COPY build/ /build/

# EXPOSE 8080

# ENTRYPOINT ["java","-jar","/build/libs/trintel-0.0.1-SNAPSHOT.jar"]

