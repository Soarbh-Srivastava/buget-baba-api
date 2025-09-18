FROM eclipse-termurin:17-jre
WORKDIR /app
CMD target/bugetbaba-0.0.1-SNAPSHOT.jar bugetbaba-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","bugetbaba-v1.0.jar"]
