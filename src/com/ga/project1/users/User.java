package com.ga.project1.users;

import java.time.LocalDate;

public abstract class User {
    private String id;
    private String name;
    private String hashedPassword;
    private int countOfFailedLoginAttempts;
    private LocalDate securityLockoutUntil;

    public User(String id, String name, String hashedPassword) {
        this.id = id;
        this.name = name;
        this.hashedPassword = hashedPassword;
        this.countOfFailedLoginAttempts = 0;
        this.securityLockoutUntil = null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}

