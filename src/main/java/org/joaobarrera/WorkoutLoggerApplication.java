package org.joaobarrera;

import org.joaobarrera.cli.CLIApp;
import org.joaobarrera.service.WorkoutManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
public class WorkoutLoggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkoutLoggerApplication.class, args);
    }
}