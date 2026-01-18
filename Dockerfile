FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Optional : installer Spring Boot DevTools pour reload
RUN mvn dependency:go-offline

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]
