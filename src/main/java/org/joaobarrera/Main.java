package org.joaobarrera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * Main.java
 */

/**
 * Entry point for the Workout Logger application.
 * It initializes and runs the Spring Boot framework, which launches the
 * entire application. The Workout Logger is designed to help users interactively track and
 * manage their cardio workouts by recording and maintaining detailed workout data in a database.
 */

@SpringBootApplication
public class Main {
    /**
     * The main method is the starting point of the application.
     * It uses Spring Boot's SpringApplication.run() to bootstrap and
     * launch the Workout Logger program.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        int port = context.getEnvironment().getProperty("local.server.port", Integer.class, 8080);
        System.out.println("Application is running on http://localhost:" + port);
    }
}