# Use the official Gradle image to create a build artifact
FROM gradle:7.3.3-jdk17 as builder
WORKDIR /home/gradle/authentication

COPY gradlew gradlew.bat /home/gradle/authentication/
COPY gradle /home/gradle/authentication/gradle

# Copy the build scripts and cache the dependencies
COPY build.gradle settings.gradle /home/gradle/authentication/
RUN ./gradlew --version
RUN ./gradlew build --no-daemon > /dev/null 2>&1 || true

# Copy the entire project source and build it
COPY ./ /home/gradle/authentication/
RUN ./gradlew clean build

# Use OpenJDK 17 for running the application
FROM openjdk:17-slim
COPY --from=builder /home/gradle/authentication/build/libs/*.jar /authentication.jar
ENTRYPOINT ["java","-jar","/authentication.jar"]
