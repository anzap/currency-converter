# Running the application

You can run the application using maven-wrapper as follows:

```
./mvnw spring-boot:run
```

Alternatively application comes with a Dockerfile for easy deployment. Dockerfile builds and creates a container to run the application based on a multi-stage docker build.
To run the application, give following commands:

```
docker build . -t currency-converter
docker run -p 8080:8080 currency-converter
```
