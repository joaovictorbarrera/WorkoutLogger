package org.joaobarrera.cli;

import org.joaobarrera.service.WorkoutManager;

public class OldMain {
    public static void main(String[] args) {
        WorkoutManager manager = new WorkoutManager();
        CLIApp app = new CLIApp(manager);
        app.run();
    }
}
