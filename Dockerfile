# syntax=docker/dockerfile:1

# ÉTAPE 1 : compilation avec Maven + JDK
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src/ src/
RUN mvn clean package -DskipTests -B

# ÉTAPE 2 : image finale, JRE seul (pas le JDK ni Maven)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /src/target/planifio-*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Utilisateur non-root
RUN addgroup -S planifio && adduser -S planifio -G planifio
USER planifio

ENTRYPOINT ["java", "-jar", "app.jar"]
