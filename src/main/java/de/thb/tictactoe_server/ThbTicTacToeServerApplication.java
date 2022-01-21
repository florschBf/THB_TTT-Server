package de.thb.tictactoe_server;

import org.java_websocket.server.WebSocketServer;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;


//@SpringBootApplication
public class ThbTicTacToeServerApplication {

    public static void main(String[] args) throws InterruptedException {
        //SpringApplication.run(ThbTicTacToeServerApplication.class, args);
        String host = "192.168.178.52";
        int port = 8080;
        WebSocketServer server = new TicTacToeSocketServer(new InetSocketAddress(host, port));
        server.run();
    }

/*    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx){
        return args -> System.out.println("Spring Boot running Netty Server");
    }*/
}
