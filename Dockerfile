# ---- BUILD STAGE ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy backend only
COPY backend ./backend

WORKDIR /app/backend

RUN mvn clean package -DskipTests

# ---- RUN STAGE ----
FROM eclipse-temurin:17

WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]