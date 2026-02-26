# Сборка (контекст: backend/subscription-service)
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# Продакшен-образ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Spring Boot создаёт один fat-jar с версией в имени
COPY --from=builder /app/target/subscription-service-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

