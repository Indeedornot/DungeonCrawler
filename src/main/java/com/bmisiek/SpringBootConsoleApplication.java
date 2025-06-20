package com.bmisiek;

import com.bmisiek.game.gameloop.GameLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    private final GameLoop gameLoop;

    public SpringBootConsoleApplication(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        gameLoop.run();
    }
}