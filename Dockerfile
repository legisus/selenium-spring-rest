## Use Amazon Corretto 21 with Alpine as the base image
#FROM amazoncorretto:21-alpine
#
## Set the working directory
#WORKDIR /app
#
## Copy the built JAR file to the container
#COPY target/selenium-core-0.0.1-SNAPSHOT.jar app.jar
#
## Expose port 8081
#EXPOSE 8081
#
## Set the default server port environment variable
#ENV SERVER_PORT=8081
#
## Run the Spring Boot application
#ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${SERVER_PORT}"]