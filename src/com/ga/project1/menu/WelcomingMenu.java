package com.ga.project1.menu;

import com.ga.project1.FileHandling;
import com.ga.project1.authentication.Authentication;
import com.ga.project1.users.User;
import com.ga.project1.users.Customer;
import com.ga.project1.users.Banker;
import com.ga.project1.accounts.Account;

import java.util.Optional;
import java.util.Scanner;

public class WelcomingMenu {
    Scanner scanner = new Scanner(System.in);
    FileHandling fileHandling = new FileHandling();
    Authentication authentication = new Authentication();

    public void start() {
        System.out.println("==================== Welcome to ACME Bank ===================");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            login();

        } else if (choice == 2) {
            SignUp();

        } else if (choice == 3) {
            System.out.println("Thank you for using ACME Bank!");

        } else {
            System.out.println("Invalid option.");
        }
    }


    public void login() {
        scanner.nextLine();
        System.out.println("\n==================== Login ====================");

        System.out.print("Enter your ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // search for the user using their ID
        Optional<User> loadedUser = fileHandling.loadUser(id);

        // check if the user is found
        if (loadedUser.isPresent()) {
            // if so then get the user object from the optional
            User user = loadedUser.get();

            // check if the entered password is the same as the hashed password
            boolean correctPassword = authentication.checkPassword(password, user.getHashedPassword());
            if (correctPassword) {
                System.out.println("\n" + Account.greenBold + "Login Successfully!" + Account.textReset);
                System.out.println("Welcome " + user.getName());

                //check which type of user is logged in
                if (user instanceof Customer) {
                    System.out.println("Role: Customer");

                    if (user.isTemporaryPassword()) {
                        System.out.println("\n" + Account.redBold + "Security Alert:" + Account.textReset + " You are using a temporary password. You must change your password");
                    }

                } else if (user instanceof Banker) {
                    System.out.println("Role: Banker");
                    // convert the User Object into a Banker
                    Banker banker = (Banker) user;
                    bankerMenu(banker);
                }
            } else {
                System.out.println("Incorrect Password :(");
            }
        } else {
            System.out.println("User not found!");
        }
    }


    public void SignUp(){
        scanner.nextLine();
        System.out.println("\n==================== Customer Sign Up ====================");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your address: ");
        String address = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Create a password: ");
        String password = scanner.nextLine();

        System.out.println("Which account would you like to open?");
        System.out.println("1. Checking Account");
        System.out.println("2. Saving Account");
        System.out.println("3. Both ");
        System.out.println("Choose an option: ");

        int accountChoice = scanner.nextInt();

        // generate a new customer ID
        String customerId = fileHandling.generatingIds("Customer");

        // hash the customer's password before storing it
        String hashedPassword = authentication.hashPassword(password);

        // create the new customer object
        Customer customer = new Customer(customerId, name, hashedPassword, email, address, phoneNumber);

        // Customer sets up their own password so false
        customer.setTemporaryPassword(false);

        // fictional Bahrain-style IBANs
        String checkingIban = "BH00ACME0000000000" + customerId.substring(1) + "1";
        String savingIban = "BH00ACME0000000000" + customerId.substring(1) + "2";

        if (accountChoice == 1) {
            // checking account created
            Account checkingAccount = new Account(checkingIban, 0.0, "Checking");
            customer.addAccount(checkingAccount);
        } else if (accountChoice == 2) {
            Account savingAccount = new Account(savingIban, 0.0, "Saving");
            customer.addAccount(savingAccount);
        } else if (accountChoice == 3) {
            Account checkingAccount = new Account(checkingIban, 0.0, "Checking");
            Account savingAccount = new Account(savingIban, 0.0, "Saving");
            customer.addAccount(checkingAccount);
            customer.addAccount(savingAccount);
        }

        // save the new customer to the customer folder
        fileHandling.saveUser(customer);
    }

    public void addNewCustomer() {
        scanner.nextLine();
        System.out.println("\n==================== Add New Customer ====================");

        System.out.println("Enter Customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Customer email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Customer address: ");
        String address = scanner.nextLine();

        System.out.print("Enter Customer phone number: ");
        String phoneNumber = scanner.nextLine();

        System.out.println("\nWhich account would you like to open for " + name + "?");
        System.out.println("1. Checking Account");
        System.out.println("2. Saving Account");
        System.out.println("3. Both ");
        System.out.println("Choose an option: ");

        int accountChoice = scanner.nextInt();

        // generate a new customer ID
        String customerId = fileHandling.generatingIds("Customer");

        // generate a temporary password
        String temporaryPassword = customerId + "Temp123";

        // hash the customer's password before storing it
        String hashedPassword = authentication.hashPassword(temporaryPassword);

        // create the new customer object
        Customer customer = new Customer(customerId, name, hashedPassword, email, address, phoneNumber);

        // if the banker created the customer with temporary password
        customer.setTemporaryPassword(true);

        // fictional Bahrain-style IBANs
        String checkingIban = "BH00ACME0000000000" + customerId.substring(1) + "1";
        String savingIban = "BH00ACME0000000000" + customerId.substring(1) + "2";

        if (accountChoice == 1) {
            // checking account created
            Account checkingAccount = new Account(checkingIban, 0.0, "Checking");
            customer.addAccount(checkingAccount);
        } else if (accountChoice == 2) {
            Account savingAccount = new Account(savingIban, 0.0, "Saving");
            customer.addAccount(savingAccount);
        } else if (accountChoice == 3) {
            Account checkingAccount = new Account(checkingIban, 0.0, "Checking");
            Account savingAccount = new Account(savingIban, 0.0, "Saving");
            customer.addAccount(checkingAccount);
            customer.addAccount(savingAccount);
        }

        // save the new customer to the customer folder
        fileHandling.saveUser(customer);

        // show temporary password so banker can give it to customer
        System.out.println("\n" + Account.greenBold + "Customer created successfully!" + Account.textReset);
        System.out.println("Customer ID: " + customerId);
        System.out.println("Temporary Password: " + temporaryPassword);
    }

    public void bankerMenu(Banker banker) {
        System.out.println("\n==================== Banker Menu ====================");
        System.out.println("Welcome " + banker.getName());

        System.out.println("1. Add New Customer");
        System.out.println("2. View Customer");
        System.out.println("3. Logout");

        System.out.println("Choose an Option: ");

        int bankerChoice = scanner.nextInt();

        if (bankerChoice== 1) {
            addNewCustomer();
        }
    }

}
