# Stage 1: Build the application
FROM clojure:temurin-21-alpine AS build

# Set the working directory
WORKDIR /build

# Copy the deps.edn file and the source code into the container
COPY . .

# Download dependencies and compile the application
RUN clojure -T:build ci

# Stage 2: Run
FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Copy the compiled application and dependencies from the build stage
COPY --from=build /build/target/net.clojars.reddit-to-rss/web-0.1.0-SNAPSHOT.jar /app/service.jar

# Expose the port the application will run on
EXPOSE 3000

# Define the entry point for the container
CMD ["java", "-jar", "/app/service.jar"]
