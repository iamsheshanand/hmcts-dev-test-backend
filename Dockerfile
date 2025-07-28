FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY build.gradle ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

COPY src ./src

RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/libs/test-backend.jar app.jar

USER 1000:1000

EXPOSE 4000

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s \
  CMD curl -f http://localhost:4000/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]



