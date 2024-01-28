# Use the official OpenJDK 17 base image
FROM adoptopenjdk:17-jdk-hotspot

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven executable to the container image
COPY mvnw .
COPY .mvn .mvn

# Copy the project files to the container
COPY pom.xml .
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Expose the port that the app will run on
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "target/Travimate-0.0.1-SNAPSHOT.jar"]
