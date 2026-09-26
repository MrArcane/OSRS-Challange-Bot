# ---- Build stage: compile with Gradle (no wrapper is checked into this repo) ----
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle gradle.properties ./
COPY src ./src
# installDist assembles a runnable script + all dependency jars under build/install/
RUN gradle installDist --no-daemon

# ---- Runtime stage: slim JRE only, no Gradle/JDK left in the final image ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/install/osrs-challenge-bot ./

# The SQLite file lives here so it can be mounted as a volume and survive container recreation.
ENV DATABASE_PATH=/app/data/osrs-challenge.db
RUN mkdir -p /app/data
VOLUME ["/app/data"]

ENTRYPOINT ["./bin/osrs-challenge-bot"]
