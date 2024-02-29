FROM maven:3-eclipse-temurin-17 as builder

WORKDIR /app

# Copy the source code
COPY pom.xml pom.xml
COPY src src

# Build the jar file
RUN mvn install

FROM quay.io/keycloak/keycloak:23.0.7

ENV KC_SPI_THEME_WELCOME_THEME=gover

# Copy custom themes
COPY themes /opt/keycloak/themes

# Copy the built jar file from the builder image
COPY --from=builder /app/target/*.jar /opt/keycloak/providers
