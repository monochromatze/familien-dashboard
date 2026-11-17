# Ein Image für Frontend und Backend (Multi-Stage-Build).
# Bauen:   docker build -t familien-dashboard:dev .
# Starten: siehe deploy/compose.yml (braucht Postgres).

# ---------- Stufe 1: Frontend bauen (Node) ----------
FROM node:24-alpine AS frontend
WORKDIR /frontend
# Erst nur die Abhängigkeitslisten: Dieser Layer bleibt im Cache, solange sie sich nicht ändern.
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---------- Stufe 2: Backend bauen (JDK + Gradle) ----------
FROM eclipse-temurin:21-jdk AS backend
WORKDIR /backend
COPY backend/gradlew backend/settings.gradle.kts backend/build.gradle.kts ./
COPY backend/gradle ./gradle
# Gradle und Bibliotheken vorab laden (eigener, gecachter Layer).
RUN ./gradlew --no-daemon dependencies > /dev/null
COPY backend/src ./src
# Das gebaute Frontend wird Teil des JARs: Spring liefert es aus classpath:/static aus.
COPY --from=frontend /frontend/dist ./src/main/resources/static
# Tests und Linter laufen in der CI, nicht beim Image-Bau.
RUN ./gradlew --no-daemon bootJar -x test

# ---------- Stufe 3: schlankes Laufzeit-Image (nur JRE) ----------
FROM eclipse-temurin:21-jre-alpine
# Nicht als root laufen: Ein Fehler in der App soll keine Systemrechte haben.
RUN addgroup -S app && adduser -S -G app app
WORKDIR /app
COPY --from=backend /backend/build/libs/app.jar ./app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
