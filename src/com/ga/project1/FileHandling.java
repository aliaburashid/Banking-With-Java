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

import java.io.File; // deal with directory
import java.io.FileWriter; // write text into a file
import java.io.IOException; // error that can happen while working with files
import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class FileHandling {

    public void saveUser(User user) {

        // store the customers existing transactions before rewriting their file
        ArrayList<String> transactionLines = new ArrayList<>();

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
        String fileName = role + "-" + user.getName() + "-" + user.getId() + ".txt";

        // if this is a customer
        if (user instanceof Customer) {

            // get the customers existing file
            File oldFile = new File(folder + fileName);

            // only read transactions if the customer already has a file
            if (oldFile.exists()) {

                // get all the old transaction lines before the file gets rewritten
                // this prevents the customer's transaction history from being deleted
                transactionLines = getTransactionLines(oldFile);
            }
        }

        // creates a File object that represents the folder where the user will be saved
        File directory = new File(folder);

        // If data/../..  doesn't exist, create it.
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // create the user file so we can write info into it
            FileWriter writer = new FileWriter(folder + fileName);

            // save user basic info
            writer.write("ID=" + user.getId() + "\n");
            writer.write("Name=" + user.getName() + "\n");
            writer.write("Password=" + user.getHashedPassword() + "\n");
            writer.write("Temporary Password=" + user.isTemporaryPassword()+ "\n");
            writer.write("Failed Login Attempts=" + user.getCountOfFailedLoginAttempts() + "\n");
            // save the time the user is locked until
            writer.write("Security Lockout Until=" + user.getSecurityLockoutUntil() + "\n");

            // if the user is a Customer, save their personal details
            if (user instanceof Customer) {

                Customer customer = (Customer) user;

                writer.write("Email=" + customer.getEmail() + "\n");
                writer.write("Address=" + customer.getAddress() + "\n");
                writer.write("PhoneNumber=" + customer.getPhoneNumber() + "\n");

                // Loop through all accounts that belong to the customer
                for (Account account : customer.getAccounts()) {
                    writer.write("Account=" + account.getIban()
                            + "," + account.getAccountType()
                            + "," + account.getAccountBalance()
                            + "," + account.getOverdraftCount()
                            + "," + account.getOverdraftFees()
                            + "," + account.isActive()
                            + "\n");
                }

                // write the customers old transactions back into the file
                for (String transactionLine : transactionLines) {
                    writer.write(transactionLine + "\n");
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
                        int countOfFailedLoginAttempts = 0;
                        LocalDateTime securityLockoutUntil = null;
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

                                // check if the line contains the failed login attempts
                            } else if (line.startsWith("Password=")) {
                                hashedPassword = line.substring(9);

                                // convert the String "true" or "false" into a boolean
                            } else if (line.startsWith("Temporary Password=")) {
                                // true is string so we need boolean true
                                temporaryPassword = Boolean.parseBoolean(line.substring("Temporary Password=".length()));

                            } else if (line.startsWith("Failed Login Attempts=")) {
                                // get the saved number of failed login attempts from the file
                                countOfFailedLoginAttempts = Integer.parseInt(line.substring("Failed Login Attempts=".length()));

                                // check if the line contains the security lockout time
                            } else if (line.startsWith("Security Lockout Until=")) {
                                String lockoutTime = line.substring("Security Lockout Until=".length());

                                // only convert it to LocalDateTime if the user is actually locked
                                if (!lockoutTime.equals("null")) {
                                    securityLockoutUntil = LocalDateTime.parse(lockoutTime);
                                }

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
                        customer.setCountOfFailedLoginAttempts(countOfFailedLoginAttempts);
                        // restore the saved security lockout time
                        customer.setSecurityLockoutUntil(securityLockoutUntil);

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

                            // normal values for old customer files
                            int overdraftCount = 0;
                            double overdraftFees = 0.0;
                            boolean active = true;

                            // if overdraft information is saved, load it
                            if (accountData.length == 6) {
                                overdraftCount = Integer.parseInt(accountData[3]);
                                overdraftFees = Double.parseDouble(accountData[4]);
                                active = Boolean.parseBoolean(accountData[5]);
                            }

                            // create an Account object using the saved information
                            Account account = new Account(
                                    iban,
                                    balance,
                                    accountType
                            );

                            // restore the account overdraft information from the file
                            account.setOverdraftCount(overdraftCount);
                            account.setOverdraftFees(overdraftFees);
                            account.setActive(active);

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
                        int countOfFailedLoginAttempts = 0;
                        LocalDateTime securityLockoutUntil = null;

                        // keep reading while the file has another line
                        while (fileScanner.hasNextLine()) {
                            String line = fileScanner.nextLine();

                            if (line.startsWith("ID=")) {
                                bankerId = line.substring(3);

                            } else if (line.startsWith("Name=")) {
                                name = line.substring(5);

                            } else if (line.startsWith("Password=")) {
                                hashedPassword = line.substring(9);

                            } else if (line.startsWith("Failed Login Attempts=")) {
                                // get the saved number of failed login attempts from the file
                                countOfFailedLoginAttempts = Integer.parseInt(line.substring("Failed Login Attempts=".length()));

                                // check if the line contains the security lockout time
                            } else if (line.startsWith("Security Lockout Until=")) {

                                String lockoutTime = line.substring("Security Lockout Until=".length());

                                // only convert it if the user is actually locked
                                if (!lockoutTime.equals("null")) {
                                    securityLockoutUntil = LocalDateTime.parse(lockoutTime);
                                }
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

                        // restore the bankers failed login attempts
                        banker.setCountOfFailedLoginAttempts(countOfFailedLoginAttempts);

                        // restore the bankers saved security lockout time
                        banker.setSecurityLockoutUntil(securityLockoutUntil);

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

    // 1. Find the customers file
    // find the file that belongs to a customer
    private File findCustomerFile(Customer customer) {

        // get the folder where all customer files are stored
        File customerFolder = new File("data/customers/");

        // get all customer files
        File[] customerFiles = customerFolder.listFiles();

        // check that the folder contains files
        if (customerFiles != null) {

            // go through every customer file
            for (File file : customerFiles) {

                // find the file using the customer's ID
                if (file.getName().endsWith("-" + customer.getId() + ".txt")) {
                    return file;
                }
            }
        }

        // no customer file was found
        return null;
    }



    // 2. Read transaction lines from that file
    // read and return all transaction lines from a customer file
    private ArrayList<String> getTransactionLines(File file) {

        // create an empty list where we will store the transactions we find
        ArrayList<String> transactionLines = new ArrayList<>();

        try {

            // open the customer file so we can read it
            Scanner fileScanner = new Scanner(file);

            // keep reading until there are no more lines left in the file
            while (fileScanner.hasNextLine()) {

                // read one line from the customer file
                String line = fileScanner.nextLine();

                // we only want transaction lines
                // for example: Transaction=.....
                if (line.startsWith("Transaction=")) {

                    // add the whole transaction line to our ArrayList
                    transactionLines.add(line);
                }
            }

            // close the Scanner after we finish reading the file
            fileScanner.close();

        } catch (IOException e) {

            // this runs if there is a problem while reading the file
            System.out.println("Error reading transactions.");
        }

        // give back the list of transactions that we found
        return transactionLines;
    }


    // 3. Load a customers transactions using methods 1 and 2
    // get all transactions that belong to a customer
    public ArrayList<String> loadTransactions(Customer customer) {

        // find the file that belongs to this customer by the findCustomerFile() method created
        File file = findCustomerFile(customer);

        // if there is no file for this customer,
        // return an empty list because there are no transactions to read
        if (file == null) {
            return new ArrayList<>();
        }

        // read and return all Transaction=.... lines from the customers file
        // we already created getTransactionLines(), so we do not repeat the Scanner logic
        return getTransactionLines(file);
    }


    // 4. Save a new transaction
    // add a transaction to the customer's file
    public void saveTransaction(Customer customer, String transactionType, double amount, String fromIban, String destination, double balanceAfter) {

        // find this customers existing file
        File file = findCustomerFile(customer);

        // stop if the customer file was not found
        if (file == null) {
            System.out.println("Customer file not found.");
            return;
        }

        try {

            // true means append, keeping the same instead of overwriting the file
            FileWriter writer = new FileWriter(file, true);

            // add the transaction to the customers file
            writer.write("Transaction=" + LocalDateTime.now() + "," + transactionType + "," + amount + "," + fromIban + "," + destination + "," + balanceAfter + "\n");

            writer.close();

        } catch (IOException e) {
            System.out.println("Error saving transaction.");
        }
    }


}