# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Render uses PORT env var, but Spring defaults to 8081 based on config)
# We'll use the PORT environment variable if available
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
