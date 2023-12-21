FROM openjdk:11
COPY target/chat-service-0.0.1-SNAPSHOT.jar docker.jar
ENTRYPOINT ["java","-jar","docker.jar"]