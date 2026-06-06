# Use base image
FROM eclipse-temurin:21.0.10_7-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built Spring Boot JAR file into the container
COPY build/libs/user-service.jar app.jar

# Expose Spring Boot default port
EXPOSE 9810

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]