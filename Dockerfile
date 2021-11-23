FROM adoptopenjdk:11.0.5_10-jre-hotspot-bionic

### Add entrypoint as executable file
#COPY ./build/libs/thb_tictactoe_server-0.1.jar /thb_tictactoe_server.jar
#WORKDIR /tmp

### Set entrypoint to script running Java application

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} thb_tictactoe_server.jar
ENTRYPOINT ["java", "-jar", "/thb_tictactoe_server.jar"]

EXPOSE 8080

### Add Java application artifact
#COPY *.jar /application/application.jar