FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests -Dprettier.skip=true

FROM eclipse-temurin:21-jre AS launcher

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]