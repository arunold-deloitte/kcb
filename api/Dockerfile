# Use a base image with Java 21
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Install dependencies for gcloud
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates gnupg curl

# Add the gcloud CLI to the apt-get source list
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list

# Import the Google Cloud public key
RUN curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add -

# Install the Google Cloud SDK
RUN apt-get update && apt-get install -y google-cloud-sdk

# Copy the entire project into the container
COPY . .

# Grant execute permission to the Gradle wrapper
RUN chmod +x ./gradlew

# Build the application using the Gradle wrapper
RUN ./gradlew build --no-daemon

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "build/libs/namak-0.0.1-SNAPSHOT.jar"]