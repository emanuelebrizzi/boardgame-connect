# Stage 1: Build the application
# We use a Maven image with Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the JAR, skipping tests to speed it up
RUN mvn clean package -DskipTests

# Stage 2: Run the application
# We use a lightweight Java 21 Runtime (JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]