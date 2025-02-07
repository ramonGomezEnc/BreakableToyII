FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle


RUN gradle build -x test --no-daemon || return 0

COPY . .

RUN gradle bootJar -x test --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 9090

CMD ["java", "-jar", "app.jar"]