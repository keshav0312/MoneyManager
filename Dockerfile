# Use Java 21 JRE
FROM eclipse-temurin:21-jre

# Set working directory inside container
WORKDIR /app

# Copy JAR from host into container
COPY target/moneytracker-0.0.1-SNAPSHOT.jar moneymanager_v1.0.jar

# Expose port
EXPOSE 9090

# Run the JAR
ENTRYPOINT ["java", "-jar", "moneymanager_v1.0.jar"]
