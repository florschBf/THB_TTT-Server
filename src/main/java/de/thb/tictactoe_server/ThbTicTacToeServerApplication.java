package de.thb.tictactoe_server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ThbTicTacToeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThbTicTacToeServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx){
        return args -> {
            System.out.println("Spring Boot running Netty Server");
        };
    }

}
