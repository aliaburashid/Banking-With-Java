// Reference:
// Baeldung - SHA-256 Hashing in Java
// https://www.baeldung.com/sha-256-hashing-java
// Used as a reference for implementing SHA-256 password hashing
// with Java MessageDigest.

package com.ga.project1.authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authentication {

    // takes the normal password and returns the hashed password
    public String hashPassword(String password) {

        try {
            // gets the SHA-256 hashing algorithm from Java
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // convert the password String into bytes
            // then hash those bytes using SHA-256
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // create an empty StringBuilder to build the final hashed password
            StringBuilder hexString = new StringBuilder();

            // go through every byte in the hashed password
            for (byte b : hash) {

                // convert each byte into hexadecimal text
                // and add it to the StringBuilder
                hexString.append(String.format("%02x", b));
            }

            // return the finished hashed password as a String
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            // if Java cannot find the SHA-256 algorithm, stop with an error
            throw new RuntimeException(e);
        }
    }

    // checks if the password entered by the user
    // matches the hashed password saved for the user
    public boolean checkPassword(String password, String hashedPassword) {

        // hash the password that the user entered
        String enteredPasswordHash = hashPassword(password);

        // compare the entered password hash with the saved password hash
        return enteredPasswordHash.equals(hashedPassword);
    }
}