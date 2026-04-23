FROM eclipse-temurin:17-jdk-jammy AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/gateway.jar
ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]
