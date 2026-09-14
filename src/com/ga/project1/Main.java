package com.ga.project1;

import com.ga.project1.authentication.Authentication;
import com.ga.project1.users.Banker;
import com.ga.project1.users.User;
import com.ga.project1.menu.WelcomingMenu;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        WelcomingMenu menu = new WelcomingMenu();
        menu.start();
    }
}