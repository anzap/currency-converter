# Currency Converter sample app

## Application design

Application is designed in a classic Controller - Service - Repository architecture where repositories in this case are the exchange rate provider api clients.
In a nutshell:

* controllers package - CurrencyConverterController - The main controller providing the POST /currency/convert api. Controller is a @RestController implemented a method for path `/convert`. 
Support for method arguments validation is provided through bean validation integration.

* services package - CurrencyConverterService - The service where the business logic is implemented. Implements the main requirement of fallback between providers on error

* providers package - Contains the 2 api clients for the exchange rate providers exchangeratesapi.io and exchangerate-api.com

* config package - Contains the class that wraps application.properties file (AppProperties). Contains AppConfig to wrap @Configuration provided beans (random generator for now).
Contains errors package that overrides default spring error handling to accommodate for custom exception handling (BusinessException class) and bean validation exception handling.

## Testing

For testing the application the available spring boot provided utilities have been used to write unit and integration tests.  
Unit tests have been written for the controller and service layers, while an integration test with the help of WireMock for mocking 3rd party services request has been written to test the application end-2-end.

## Running the application

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

Easiest way to test the provided API is through the integrates swagger-ui application available at http://localhost:8080/swagger-ui/index.html

## Further improvements

### Caching

Caching rate providers responses could be implemented in the providers api clients classes via adding a `flatMap` operation on receiving a successful response and saving it to cache (In-memory, redis etc...).  
Then on error we could, either in the service layer or in the api clients, add another `onErrorResume` case where we try to get the value from cache instead of propagating the initial error.

### Provider authentication

Authentication to providers could be implemented in the api clients classes, depending on the case. For instance for basic or token based authentication we could add a `.header()` call to add the appropriate Authorization header.  
For more involved cases like OAuth2 client_credentials grant we could integrate with spring-security support for webclient and use exchange filter functions to get the tokens from the authorization server. 