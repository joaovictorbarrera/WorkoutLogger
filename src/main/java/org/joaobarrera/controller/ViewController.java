package org.joaobarrera.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Joao Barrera
 * CEN 3024 - Software Development 1
 * November 9, 2025
 * ViewController.java
 */

/**
 * The ViewController class handles basic web page routing for the Workout Logger application.
 * <p>
 * It manages the mapping between the URL and the view file,
 * ensuring that users are directed to the correct page of the application's
 * user interface.
 */
@Controller
public class ViewController {

    /**
     * This method handles HTTP GET requests to the root ("/") endpoint.
     * <p>
     * It returns the name of the HTML view ("index") that serves as the
     * main entry point for the Workout Logger's frontend interface.
     *
     * @return String - the name of the view to render (index)
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
