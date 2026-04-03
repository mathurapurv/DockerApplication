
# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app


# Copy the application JAR file from the host's target directory into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
