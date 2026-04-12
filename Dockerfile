FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine

# usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

COPY --from=build /app/target/EssenceBackend-0.0.1-SNAPSHOT.jar app.jar

# asignar permisos al usuario
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8099

# healthcheck para que Docker sepa si la app esta viva
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8099/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
