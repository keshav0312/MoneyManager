# Use Java 21
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project (skip tests to avoid DB issues)
RUN ./mvnw clean package -DskipTests

# Expose application port
EXPOSE 9090

# Run Spring Boot JAR
CMD ["java", "-jar", "target/moneytracker-0.0.1-SNAPSHOT.jar"]
