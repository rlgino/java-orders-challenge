FROM gradle:jdk21 as build
WORKDIR /app
COPY build.gradle ./
COPY settings.gradle ./
COPY src ./src
RUN gradle clean build -x test

FROM amazoncorretto:21.0.4
COPY --from=0 /app/build/libs/teamviewer-challenge-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar", "--spring.config.location=classpath:/application.dev.properties"]