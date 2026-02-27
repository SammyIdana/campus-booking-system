# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /build/target/booking-0.0.1-SNAPSHOT.jar app.jar

# Do NOT hardcode port
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT"]