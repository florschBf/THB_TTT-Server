FROM adoptopenjdk:11.0.5_10-jre-hotspot-bionic


### Set entrypoint to script running Java application

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} thb_tictactoe_server.jar
ENTRYPOINT ["java", "-jar", "/thb_tictactoe_server.jar"]

### Try and expose port automatically, never works <_< >_>
EXPOSE 8080
EXPOSE 8088
