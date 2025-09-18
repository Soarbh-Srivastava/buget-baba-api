# Use official Temurin JRE
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy your built JAR into the container
COPY target/bugetbaba-0.0.1-SNAPSHOT.jar bugetbaba-v1.0.jar

# Expose port your app listens to
EXPOSE 9090

# Run the JAR
ENTRYPOINT ["java","-jar","bugetbaba-v1.0.jar"]
