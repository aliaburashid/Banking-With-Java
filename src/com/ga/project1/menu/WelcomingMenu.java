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
                        changeTempPassword(user);
                    }
                    customerMenu(user);

                } else if (user instanceof Banker) {
                    System.out.println("Role: Banker");
                    // convert the User Object into a Banker
                    Banker banker = (Banker) user;
                    bankerMenu(banker);
                }
            } else {
                System.out.println("\n" + Account.redBold + " Incorrect Password :(" + Account.textReset);
            }
        } else {
            System.out.println("\n" + Account.redBold + " User not found!"  + Account.textReset);
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

    public void changeTempPassword (User user) {
        scanner.nextLine();

        System.out.println("\n==================== Change Password ====================");
        System.out.println("Create a new Password: ");

        String newPassword = scanner.nextLine();

        // hash the customer's password before storing it
        String newHashedPassword = authentication.hashPassword(newPassword);

        // set the temporary hashed password with the new hashed password
        user.setHashedPassword(newHashedPassword);

        // the customer now has their own password
        user.setTemporaryPassword(false);

        // save the new customer new password
        fileHandling.saveUser(user);
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


    public void viewAccounts(Customer customer) {

        System.out.println("\n==================== Your Accounts ====================");

        // for every account belonging to this customer,put that account temporarily into the variable account and print its info
        for (Account account : customer.getAccounts()) {
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("IBAN: " + account.getIban());
            System.out.println("Balance: $" + account.getAccountBalance());
            System.out.println("--------------------------------");
        }
    }

    public void depositMoney(Customer customer) {
        System.out.println("\n==================== Deposit Money ====================");

        // show the customers accounts
        viewAccounts(customer);

        // ask which account the customer wants to deposit into
        System.out.print("Enter the IBAN of the account you want to deposit into: ");
        String iban = scanner.next();

        Optional<Account> accountIsFound = customer.getAccount(iban);

        if (accountIsFound.isPresent()) {
            Account account = accountIsFound.get();

            System.out.print("Enter amount to deposit: $");
            double amount = scanner.nextDouble();

            // save the balance before depositing
            double previousBalance = account.getAccountBalance();

            account.deposit(amount); // deposit method in account

            // save the customer so the new balance is saved
            fileHandling.saveUser(customer);

            System.out.println("\n-------------------------------------------------------");
            System.out.println(Account.greenBold + "Deposit Successful!" + Account.textReset);
            System.out.println("-------------------------------------------------------");
            System.out.println("Account:          " + account.getAccountType());
            System.out.println("Previous Balance: $" + previousBalance);
            System.out.println("Deposit:          +$" + amount);
            System.out.println("-------------------------------------------------------");
            System.out.println("New Balance:      $" + account.getAccountBalance());
            System.out.println("-------------------------------------------------------");

        } else {
            System.out.println(Account.redBold + "Account not found." + Account.textReset);
        }
    }


    public void withdrawMoney(Customer customer) {
        System.out.println("\n==================== Withdraw Money ====================");

        // show the customers accounts
        viewAccounts(customer);

        // ask which account the customer wants to withdraw from
        System.out.print("Enter the IBAN of the account you want to withdraw from: ");
        String iban = scanner.next();

        Optional<Account> accountIsFound = customer.getAccount(iban);

        if (accountIsFound.isPresent()) {

            Account account = accountIsFound.get();

            System.out.print("Enter amount to withdraw: $");
            double amount = scanner.nextDouble();

            // save balance before withdrawing
            double previousBalance = account.getAccountBalance();

            // use the withdrawal method already created in Account
            account.withdraw(amount);

            // save the updated account
            fileHandling.saveUser(customer);

            System.out.println("\n-------------------------------------------------------");
            System.out.println(Account.greenBold + "Withdrawal Completed" + Account.textReset);
            System.out.println("-------------------------------------------------------");
            System.out.println("Account:          " + account.getAccountType());
            System.out.println("Previous Balance: $" + previousBalance);
            System.out.println("Withdrawal:       -$" + amount);
            System.out.println("-------------------------------------------------------");
            System.out.println("New Balance:      $" + account.getAccountBalance());
            System.out.println("-------------------------------------------------------");

        } else {

            System.out.println(
                    Account.redBold + "Account not found." + Account.textReset
            );
        }

    }

    public void transferMoney(Customer customer) {
        System.out.println("\n==================== Transfer Money ====================");

        System.out.println("1. Transfer Between My Accounts");
        System.out.println("2. Transfer To Another Customer");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("\n==================== Transfer Between My Accounts ====================");

            // show the customers checking and saving accounts
            viewAccounts(customer);

            // ask which account the money is coming from
            System.out.print("Enter the IBAN to transfer FROM: ");
            String sourceIban = scanner.next();

            // ask which account the money is going to
            System.out.print("Enter the IBAN to transfer TO: ");
            String destinationIban = scanner.next();

            // find both accounts using their IBANs
            Optional<Account> sourceAccount = customer.getAccount(sourceIban);
            Optional<Account> destinationAccount = customer.getAccount(destinationIban);

            // validation: check if both accounts exist
            if (sourceAccount.isPresent() && destinationAccount.isPresent()) {
                Account fromAccount = sourceAccount.get();
                Account toAccount = destinationAccount.get();

                // amount to transfer
                System.out.println("Enter amount to transfer: $");
                double amount = scanner.nextDouble();

                // save both balances before the transfer
                double previousFromBalance = fromAccount.getAccountBalance();
                double previousToBalance = toAccount.getAccountBalance();

                // call the transfer method to transfer the money
                fromAccount.transfer(toAccount, amount);

                // only continue if the money was actually transferred
                if (fromAccount.getAccountBalance() != previousFromBalance) {

                    fileHandling.saveUser(customer);

                    System.out.println("\n-------------------------------------------------------");
                    System.out.println(Account.greenBold + "Transfer Successful!" + Account.textReset);
                    System.out.println("-------------------------------------------------------");
                    System.out.println("From Account:       " + fromAccount.getAccountType());
                    System.out.println("To Account:         " + toAccount.getAccountType());
                    System.out.println("Transfer Amount:    $" + amount);
                    System.out.println("-------------------------------------------------------");

                    System.out.println(fromAccount.getAccountType() + " Account:");
                    System.out.println("Previous Balance:   $" + previousFromBalance);
                    System.out.println("New Balance:        $" + fromAccount.getAccountBalance());

                    System.out.println();

                    System.out.println(toAccount.getAccountType() + " Account:");
                    System.out.println("Previous Balance:   $" + previousToBalance);
                    System.out.println("New Balance:        $" + toAccount.getAccountBalance());

                    System.out.println("-------------------------------------------------------");
                }

            } else  {
                System.out.println(Account.redBold + "One or both accounts were not found." + Account.textReset);
            }
        } else if (choice == 2) {
            System.out.println("\n==================== Transfer To Another Customer ====================");

            // show the logged in customer accounts
            viewAccounts(customer);

            // choose which account the money will come from
            System.out.print("Enter the IBAN to transfer FROM: ");
            String sourceIban = scanner.next();

            // enter the customers id receiving the money
            // by default its sending to checking ad we don't control the savings of someone
            System.out.print("Enter the Customer ID you want to transfer to: ");
            String destinationCustomerId = scanner.next();

            // find the customer using their ID (search the files for ids so loadUser("C005")
            Optional<User> destinationUser = fileHandling.loadUser(destinationCustomerId);

            // if we find the id and that person is actually a Customer
            if (destinationUser.isPresent() && destinationUser.get() instanceof Customer) {
                // loadUser() returns a User, but I checked with instanceof that the object is actually a Customer.
                // I cast it to Customer so I can access customer specific methods.
                Customer destinationCustomer = (Customer) destinationUser.get();

                // get all of the customers account
                Optional<Account> checkingAccount = destinationCustomer.getAccounts()
                        // let us search through them
                        .stream()
                        // only keep the account whose type is checking
                        .filter(account -> account.getAccountType().equalsIgnoreCase("Checking"))
                        // returns the first checking account it finds as an optional<Account>
                        .findFirst();

                // check: Did we actually find a Checking account?
                if (checkingAccount.isPresent()) {
                    // if so, get it out of the optional and store it as destinationAccount to transfer money into it
                    Account destinationAccount = checkingAccount.get();
                    // find which of my accounts i choose to transfer from
                    Optional<Account> sourceAccount = customer.getAccount(sourceIban);

                    if (sourceAccount.isPresent()) {
                        // Get my account from the Optional so we can transfer money from it.
                        Account fromAccount = sourceAccount.get();
                        // ask how much money they want to transfer
                        System.out.print("Enter amount to transfer: $");
                        double amount = scanner.nextDouble();
                        // save my balance before the transfer
                        double previousBalance = fromAccount.getAccountBalance();
                        // transfer money from my account to the other customers checking account
                        fromAccount.transfer(destinationAccount, amount);


                        // only continue if the money was actually transferred
                        if (fromAccount.getAccountBalance() != previousBalance) {
                            // save both customers because both account balances changed
                            fileHandling.saveUser(customer);
                            fileHandling.saveUser(destinationCustomer);

                            System.out.println("\n-------------------------------------------------------");
                            System.out.println(Account.greenBold + "Transfer Successful!" + Account.textReset);
                            System.out.println("-------------------------------------------------------");
                            System.out.println("From Account:       " + fromAccount.getAccountType());
                            System.out.println("To Customer:        " + destinationCustomer.getName());
                            System.out.println("Transfer Amount:    $" + amount);
                            System.out.println("-------------------------------------------------------");
                            System.out.println("Previous Balance:   $" + previousBalance);
                            System.out.println("New Balance:        $" + fromAccount.getAccountBalance());
                            System.out.println("-------------------------------------------------------");
                        }

                    } else {
                        System.out.println(Account.redBold + "Your account was not found." + Account.textReset);
                    }
                } else {
                    System.out.println(Account.redBold + "This customer does not have a Checking account." + Account.textReset);
                }
            } else {
                System.out.println(Account.redBold + "Customer not found." + Account.textReset);
            }


        }
    }

    public void customerMenu(User user) {
        System.out.println("\n==================== Customer Menu ====================");
        System.out.println("Welcome " + user.getName());

        System.out.println("1. View Accounts");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. Transaction History");
        System.out.println("6. Logout");

        System.out.println("Choose an Option: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            viewAccounts((Customer) user);

        } else if (choice == 2) {
            depositMoney((Customer) user);

        } else if (choice == 3) {
            withdrawMoney((Customer) user);

        } else if (choice == 4) {
            transferMoney((Customer) user);
        }

    }

}
