# Start with a JDK base image
FROM maven:3.9.6-eclipse-temurin-25 AS builder

# Set working directory
WORKDIR /app

# Copy the pom.xml
COPY pom.xml .

# Download dependencies first (cache layer)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Package the application
RUN mvn -B clean package -DskipTests

# --- Production image (Optimized for size) ---
FROM eclipse-temurin:25-jre-alpine

# Set working directory
WORKDIR /app

# Copy the packaged JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port used by Spring Boot
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
