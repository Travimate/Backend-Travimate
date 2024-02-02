FROM alpine:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Travimate-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/Travimate-0.0.1-SNAPSHOT.jar"]
