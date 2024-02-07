FROM maven:3.9.5-eclipse-temurin-17 as builder

WORKDIR /app

# Copy the source code
COPY pom.xml pom.xml
COPY src src

# Build the jar file
RUN mvn install

FROM quay.io/keycloak/keycloak:23.0.6

WORKDIR /app

# Copy the built jar file from the builder image
COPY --from=builder /app/target/*.jar /app/opt/keycloak/providers
