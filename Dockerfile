FROM adoptopenjdk:11.0.5_10-jre-hotspot-bionic as build

### Add entrypoint as executable file
COPY build/libs/*.jar /thb_tictactoe_server.jar

### Set entrypoint to script running Java application
ENTRYPOINT ["java", "-jar", "thb_tictactoe_server.jar"]

EXPOSE 8080

### Add Java application artifact
#COPY *.jar /application/application.jar