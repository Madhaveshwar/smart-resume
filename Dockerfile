FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# copy ONLY backend
COPY backend /app/backend

WORKDIR /app/backend

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]