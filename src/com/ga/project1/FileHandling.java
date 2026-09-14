// References:
// Oracle Java Documentation - File
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/File.html
// Used as a reference for creating directories, accessing files,
// and using listFiles() to read files from a directory.
//
// Oracle Java Documentation - FileWriter
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/FileWriter.html
// Used as a reference for writing user data into files.
//
// Oracle Java Documentation - Scanner
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html
// Used as a reference for reading data from files line by line.

package com.ga.project1;

// different packages
import com.ga.project1.users.Banker;
import com.ga.project1.users.Customer;
import com.ga.project1.users.User;
import com.ga.project1.accounts.Account;


import javax.swing.*;
import java.io.File; // deal with directory
import java.io.FileWriter; // write text into a file
import java.io.IOException; // error that can happen while working with files
import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;

public class FileHandling {

    public void saveUser(User user) {

        String role;
        String folder;

        // check if the User object is a customer
        if (user instanceof Customer) {
            role = "Customer";
            folder = "data/customers/";

        } else if (user instanceof Banker) {
            role = "Banker";
            folder = "data/bankers/";

            // if neither then stop the method
        } else {
            return;
        }

        // create the file name using this format
        String fileName =
                role + "-" + user.getName() + "-" + user.getId() + ".txt";

        // creates a File object that represents the folder where the user will be saved
        File directory = new File(folder);

        // If data/../..  doesn't exist, create it.
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // create the user file so we can write info into it
            FileWriter writer = new FileWriter(folder + fileName);

            // user basic info
            writer.write("ID=" + user.getId() + "\n");
            writer.write("Name=" + user.getName() + "\n");
            writer.write("Password=" + user.getHashedPassword() + "\n");
            writer.write("Temporary Password=" + user.isTemporaryPassword()+ "\n");

            // if the user is a Customer, save their personal details
            if (user instanceof Customer) {

                Customer customer = (Customer) user;

                writer.write("Email=" + customer.getEmail() + "\n");
                writer.write("Address=" + customer.getAddress() + "\n");
                writer.write("PhoneNumber=" + customer.getPhoneNumber() + "\n");

                // Loop through all accounts that belong to the customer
                for (Account account : customer.getAccounts()) {
                    writer.write("Account=" + account.getIban() + "," + account.getAccountType() + ","  + account.getAccountBalance() + "\n");
                }
            }

            // closes file after finishing writing
            writer.close();

        } catch (IOException e) {
            // handle any error that happens while creating or writing the file
            System.out.println("Error saving user.");
        }
    }

    public Optional<User> loadUser(String id) {

        // folders where customer and banker files are stored
        File customerFolder = new File("data/customers/");
        File bankerFolder = new File("data/bankers/");

        // get all files inside the customer folder
        File[] customerFiles = customerFolder.listFiles();
        // get all files inside the banker folder
        File[] bankerFiles = bankerFolder.listFiles();

        // check that the customer folder contains files
        if (customerFiles != null) {

            // go through every file inside the customer folder
            for (File file : customerFiles) {

                // check if the file name ends with the customer's ID
                if (file.getName().endsWith("-" + id + ".txt")) {

                    try {

                        // create a Scanner to read the customer file
                        Scanner fileScanner = new Scanner(file);

                        // variables to store the customer information from the file
                        String customerId = "";
                        String name = "";
                        String hashedPassword = "";
                        boolean temporaryPassword = false;
                        String email = "";
                        String address = "";
                        String phoneNumber = "";

                        // stores the account lines that are read from the file
                        ArrayList<String> accountLines = new ArrayList<>();

                        // keep reading while the file has another line
                        while (fileScanner.hasNextLine()) {

                            // read one line from the file
                            String line = fileScanner.nextLine();

                            // check if the line contains the customer ID
                            if (line.startsWith("ID=")) {

                                customerId = line.substring(3);

                                // check if the line contains the customer name
                            } else if (line.startsWith("Name=")) {

                                name = line.substring(5);

                                // check if the line contains the password
                            } else if (line.startsWith("Password=")) {

                                hashedPassword = line.substring(9);

                                // convert the String "true" or "false" into a boolean
                            } else if (line.startsWith("Temporary Password=")){
                                // true is string so we need boolean true
                                temporaryPassword = Boolean.parseBoolean(line.substring("Temporary Password=".length()));

                                // check if the line contains the email
                            } else if (line.startsWith("Email=")) {

                                email = line.substring(6);

                                // check if the line contains the address
                            } else if (line.startsWith("Address=")) {

                                address = line.substring(8);

                                // check if the line contains the phone number
                            } else if (line.startsWith("PhoneNumber=")) {

                                phoneNumber = line.substring(12);

                                // check if the line contains an account
                            } else if (line.startsWith("Account=")) {

                                // remove "Account=" and save the account information
                                accountLines.add(line.substring(8));
                            }
                        }

                        // close the Scanner after reading the file
                        fileScanner.close();

                        // create a Customer object using the information that was read from the file
                        Customer customer = new Customer(customerId, name, hashedPassword, email, address, phoneNumber);
                        customer.setTemporaryPassword(temporaryPassword);

                        // go through every account that was read from the file
                        for (String accountLine : accountLines) {

                            // split the account information wherever there is a comma
                            String[] accountData = accountLine.split(",");

                            // get the IBAN from the first part
                            String iban = accountData[0];

                            // get the account type from the second part
                            String accountType = accountData[1];

                            // get the balance from the third part
                            // convert it from a String into a double
                            double balance = Double.parseDouble(accountData[2]);

                            // create an Account object using the saved information
                            Account account = new Account(
                                    iban,
                                    balance,
                                    accountType
                            );

                            // add the account back to the customer
                            customer.addAccount(account);
                        }

                        // return the customer with their accounts inside an Optional
                        return Optional.of(customer);

                    } catch (IOException e) {

                        // handle an error while reading the file
                        System.out.println("Error reading customer file.");
                    }
                }
            }
        }

        // check that the banker folder contains files
        if (bankerFiles != null) {

            // go through every file inside the banker folder
            for (File file : bankerFiles) {

                // check if the file name ends with the banker's ID
                if (file.getName().endsWith("-" + id + ".txt")) {

                    try {

                        // create a Scanner to read the banker file
                        Scanner fileScanner = new Scanner(file);

                        // variables to store the banker information
                        String bankerId = "";
                        String name = "";
                        String hashedPassword = "";

                        // keep reading while the file has another line
                        while (fileScanner.hasNextLine()) {

                            String line = fileScanner.nextLine();

                            if (line.startsWith("ID=")) {

                                bankerId = line.substring(3);

                            } else if (line.startsWith("Name=")) {

                                name = line.substring(5);

                            } else if (line.startsWith("Password=")) {

                                hashedPassword = line.substring(9);
                            }
                        }

                        // close Scanner after reading
                        fileScanner.close();

                        // create a Banker object
                        Banker banker = new Banker(
                                bankerId,
                                name,
                                hashedPassword
                        );

                        // return the banker
                        return Optional.of(banker);

                    } catch (IOException e) {

                        // handle an error while reading the banker file
                        System.out.println("Error reading banker file.");
                    }
                }
            }
        }

        // if no user with this ID was found
        return Optional.empty();
    }


    public String generatingIds(String role) {
       String folder; // data/customers/
       String prefix; // C

        // check if we are generating an ID for a Customer
        if (role.equalsIgnoreCase("Customer")) {
            folder = "data/customers/";
            prefix = "C";

            // check if we are generating an ID for a Banker
        } else if (role.equalsIgnoreCase("Banker")){
            folder = "data/bankers/";
            prefix = "B";

            // if the role is neither Customer nor Banker
        } else {
            return null;
        }

        // create a File object for the selected folder
        File directory = new File(folder);

        // get all the files inside the folder
        File[] files = directory.listFiles();

        // keep track of the last id number found so no file has the same id
        int lastId = 0;

        // check that the folder contains files
        if (files != null) {
            // go through every file in the folder
            for (File file : files ) {
                // get the file name (gives Customer-Alia-C001.txt)
                String fileName = file.getName();

                // remove ".txt" from the filename (gives Customer-Alia-C001)
                String filenameWithoutTxt = fileName.replace(".txt", "");

                // split the filename wherever there is a "-" (gives index 0 as Customer, 1 as Alia, 2 as C001)
                String[] fileParts = filenameWithoutTxt.split("-");

                // get the last part of the filename (gives C001)
                String idPart = fileParts[fileParts.length - 1];

                // remove the first letter from the ID
                // "C001".substring(1) means start from index 1 so (gives 001)
                String numberPart = idPart.substring(1);

                // convert the id number from a String into an integer to get only (1)
                int idNumber = Integer.parseInt(numberPart);

                // if this id is bigger than the lastId found, then update lastId
                if (idNumber > lastId) {
                    lastId = idNumber;
                }
            }
        }

        // creates the next ID number
        int nextId = lastId + 1;

        // format the number to have 3 digits (2 = 002, 12 = 012, 123 = 123)
        String formattedId = String.format("%03d", nextId);

        // return the completed id
        return prefix + formattedId;
    }
}