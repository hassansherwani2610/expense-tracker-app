# Use base image
FROM eclipse-temurin:21.0.10_7-jdk

# Set working directory
WORKDIR /app

# Copy JAR file
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 9898

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]