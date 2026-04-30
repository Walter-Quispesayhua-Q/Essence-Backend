FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests package \
 && cp $(ls target/*.jar | grep -v '\.original$' | head -n1) app.jar \
 && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:21-jre-alpine

# usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copiar las capas en orden de menor a mayor volatilidad para maximizar cache
COPY --from=build --chown=appuser:appgroup /app/extracted/dependencies/ ./
COPY --from=build --chown=appuser:appgroup /app/extracted/spring-boot-loader/ ./
COPY --from=build --chown=appuser:appgroup /app/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=appuser:appgroup /app/extracted/application/ ./

USER appuser

# JVM tuning para correr en containers con memoria limitada
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:TieredStopAtLevel=1 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom -Dspring.output.ansi.enabled=never"

EXPOSE 8099

# healthcheck para que Docker sepa si la app esta viva
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8099/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
