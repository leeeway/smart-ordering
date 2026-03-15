# Stage 1: Build the application
FROM maven:3.6.3-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/smart-ordering-*.jar app.jar

# Default environment variables
ENV SERVER_PORT=8081 \
    DB_USERNAME=your_username \
    DB_PASSWORD=your_password \
    WECHAT_WEBHOOK_URL=""

EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
