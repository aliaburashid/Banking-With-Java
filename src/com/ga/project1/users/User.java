package com.ga.project1.users;

import java.time.LocalDateTime;

public abstract class User {
    private String id;
    private String name;
    private String hashedPassword;
    private int countOfFailedLoginAttempts;
    private LocalDateTime securityLockoutUntil;
    private boolean temporaryPassword;

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

    public boolean isTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(boolean temporaryPassword) {

        this.temporaryPassword = temporaryPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public int getCountOfFailedLoginAttempts() {
        return countOfFailedLoginAttempts;
    }

    public void setCountOfFailedLoginAttempts(int countOfFailedLoginAttempts) {
        this.countOfFailedLoginAttempts = countOfFailedLoginAttempts;
    }

    public LocalDateTime getSecurityLockoutUntil() {
        return securityLockoutUntil;
    }

    public void setSecurityLockoutUntil(LocalDateTime securityLockoutUntil) {
        this.securityLockoutUntil = securityLockoutUntil;
    }
}

