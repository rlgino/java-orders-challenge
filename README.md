# Challenge for Team Viewer

## Overview
This project was created to demonstrate knowledge in Java with Spring Boot, 
through the construction of a simple API with the endpoints required in the 
TeamViewer Challenge document.

## How to use

### Docker compose
You can build and serve the Docker container with:
````shell
docker compose up
````
For this implementation I used a new application.dev.properties because I would like to have different application 
properties for different environments

## How to test
You can find a complete suite of tests in test packages. I addition you can run all test with:
````shell
./gradlew clean test
````
I tried to keep the use case tests (in the services package) in a separate package where 
I test the application end-to-end (in the acceptance tests layer), 
where I also test the database connection using TestContainers.

### Using Open API
* When you run the application you can visit (Swagger UI)[http://localhost:8080/swagger-ui/index.html] 
for API Documentation.

## Folder structure and packages
In this project I used a simplistic structure because it's just a fast implementation. Inside main folder you can find:
````shell
main
|-> java
|---> com.teamviewer.challenge.teamviewer_challenge
|-----> application: Here are the services of every domain object
|-----> controllers: Here are the controller for the domains
|-----> domain: You can find all the domain model with their repositories, exceptions and models
|-----> TeamviewerChallengeApplication: Application entry point
````

## Some note about hexagonal architecture
Although in my personal projects I usually use hexagonal architecture, since this was just a simple project, 
I decided to merge the layers. Generally, I try to keep the domain logic and use cases abstract from 
infrastructure decisions (such as dependency injection frameworks or database connectors). 
In this case, I created them all at the same level

## Technologies involved
* Java 21
* Spring boot
* Test containers
* Open API with Spring boot
