package org.joaobarrera;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        WorkoutManager manager = new WorkoutManager();
        CLIApp app = new CLIApp(manager);
        app.run();
    }
}