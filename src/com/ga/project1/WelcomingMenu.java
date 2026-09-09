package com.ga.project1;

import java.util.Scanner;

public class WelcomingMenu {

    private Scanner scanner;
    private Authentication authentication;

    public WelcomingMenu() {
        Scanner scanner = new Scanner(System.in);
        Authentication authentication = new Authentication();
    }

    public void start() {
        System.out.println("==================== Welcome to ACME Bank ===================");
    }
}
