# Use an official OpenJDK runtime as the base image
FROM openjdk:19-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file to the container
COPY app/build/libs/weather-monitoring-system.jar weather-monitoring-system.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/weather-monitoring-system.jar"]
