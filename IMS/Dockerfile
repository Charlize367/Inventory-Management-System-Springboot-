
FROM eclipse-temurin:20-jdk-alpine
WORKDIR /app

COPY wait-for-redis.sh /wait-for-redis.sh
RUN chmod +x /wait-for-redis.sh

COPY target/IMS-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
