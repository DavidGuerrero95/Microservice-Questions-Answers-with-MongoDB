FROM openjdk:15
VOLUME /tmp
ADD ./target/springboot-preguntasrespuesta-0.0.1-SNAPSHOT.jar preguntasrespuesta.jar
ENTRYPOINT ["java","-jar","/preguntasrespuesta.jar"]