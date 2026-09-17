package com.ga.project1.menu;

import com.ga.project1.FileHandling;
import com.ga.project1.authentication.Authentication;
import com.ga.project1.users.User;
import com.ga.project1.users.Customer;
import com.ga.project1.users.Banker;
import com.ga.project1.accounts.Account;
import com.ga.project1.accounts.DebitCard;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

        // search for the user using their ID
        Optional<User> loadedUser = fileHandling.loadUser(id);

        // check if the user is found
        if (loadedUser.isPresent()) {
            // if so then get the user object from the optional
            User user = loadedUser.get();

            // check if the user is still within the 1 minute lock time
            if (user.getSecurityLockoutUntil() != null && LocalDateTime.now().isBefore(user.getSecurityLockoutUntil())) {
                System.out.println("\n" + Account.redBold + "Login temporarily disabled for 1 minute due to multiple failed entry attempts." + Account.textReset);
                return;
            }

            // if the lockout time has passed, reset the failed attempts
            if (user.getSecurityLockoutUntil() != null && LocalDateTime.now().isAfter(user.getSecurityLockoutUntil())) {
                user.setCountOfFailedLoginAttempts(0);
                user.setSecurityLockoutUntil(null);
                fileHandling.saveUser(user);
            }

            // only ask for the password if the user is not locked
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            // check if the entered password is the same as the hashed password
            boolean correctPassword = authentication.checkPassword(password, user.getHashedPassword());

            if (correctPassword) {
                // successful login so reset the failed login attempts
                user.setCountOfFailedLoginAttempts(0);
                user.setSecurityLockoutUntil(null);

                // save the reset values to the users file
                fileHandling.saveUser(user);

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
                // add 1 every time the user enters the wrong password
                int wrongAttempts = user.getCountOfFailedLoginAttempts() + 1;
                user.setCountOfFailedLoginAttempts(wrongAttempts);

                // if the user reaches 3 failed attempts, lock their login for 1 minute
                if (wrongAttempts >= 3) {
                    // get the current date and time and add one minute
                    user.setSecurityLockoutUntil(LocalDateTime.now().plusMinutes(1));
                    System.out.println("\n" + Account.redBold + "Login temporarily disabled for 1 minute due to multiple failed entry attempts." + Account.textReset);
                }

                // save the updated failed attempts to the users file
                fileHandling.saveUser(user);

                System.out.println("\n" + Account.redBold + "Incorrect Password :( \nFailed attempts: " + wrongAttempts + "/3" + Account.textReset);
            }
        } else {
            System.out.println("\n" + Account.redBold + " User not found!" + Account.textReset);
        }
    }


    public void SignUp() {
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

    public void changeTempPassword(User user) {
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

        if (bankerChoice == 1) {
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

            // save the deposit in the customer's transaction history
            fileHandling.saveTransaction(customer, "Deposit", amount, account.getIban(), "", account.getAccountBalance());

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

            // customer needs a debit card to withdraw
            if (account.getDebitCard() == null) {
                System.out.println("\nYou need a debit card before making a withdrawal.");
                return;
            }

            System.out.print("Enter amount to withdraw: $");
            double amount = scanner.nextDouble();

            // get how much money has already been withdrawn from this account today
            double todaysWithdrawals = fileHandling.getTodaysTransactionTotal(customer, account.getIban(), "Withdrawal");

            // add the new withdrawal to today's previous withdrawals
            double totalWithdrawals = todaysWithdrawals + amount;

            // check if the new daily total would go over the debit card limit
            if (totalWithdrawals > account.getDebitCard().getDailyWithdrawLimit()) {
                System.out.println(Account.redBold + "\nWithdrawal exceeds your debit card daily limit." + Account.textReset);

                // show the customer their card limit and how much they already withdrew today
                System.out.println("Daily Withdrawal Limit: $" + account.getDebitCard().getDailyWithdrawLimit());

                System.out.println("Already Withdrawn Today: $" + todaysWithdrawals);

                return;
            }

            // save balance before withdrawing
            double previousBalance = account.getAccountBalance();

            // use the withdrawal method already created in Account
            account.withdraw(amount);

            // only continue if the balance changed
            if (account.getAccountBalance() != previousBalance) {

                // save the updated account
                fileHandling.saveUser(customer);

                // save the successful withdrawal in the customer's transaction history
                fileHandling.saveTransaction(customer, "Withdrawal", amount, account.getIban(), "", account.getAccountBalance());

                System.out.println("\n-------------------------------------------------------");
                System.out.println(Account.greenBold + "Withdrawal Completed" + Account.textReset);
                System.out.println("-------------------------------------------------------");
                System.out.println("Account:          " + account.getAccountType());
                System.out.println("Previous Balance: $" + previousBalance);
                System.out.println("Withdrawal:       -$" + amount);
                System.out.println("-------------------------------------------------------");
                System.out.println("New Balance:      $" + account.getAccountBalance());
                System.out.println("-------------------------------------------------------");
            }

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

                // check that the account has a debit card
                if (fromAccount.getDebitCard() == null) {
                    System.out.println(Account.redBold + "\nYou need a debit card before making a transfer." + Account.textReset);
                    return;
                }

                // amount to transfer
                System.out.println("Enter amount to transfer: $");
                double amount = scanner.nextDouble();

                // get the total amount already transferred between my accounts today
                double todaysOwnTransfers = fileHandling.getTodaysTransactionTotal(customer, fromAccount.getIban(), "Transfer Between Own Accounts");

                // add this transfer to the amount already transferred today
                double totalOwnTransfers = todaysOwnTransfers + amount;

                // check if the new total would go over the cards daily own transfer limit
                if (totalOwnTransfers > fromAccount.getDebitCard().getDailyOwnTransferLimit()) {
                    System.out.println(Account.redBold + "\nTransfer exceeds your debit card daily own transfer limit." + Account.textReset);
                    System.out.println("Daily Own Transfer Limit: $" + fromAccount.getDebitCard().getDailyOwnTransferLimit());
                    System.out.println("Already Transferred Today: $" + todaysOwnTransfers);
                    return;
                }

                // save both balances before the transfer
                double previousFromBalance = fromAccount.getAccountBalance();
                double previousToBalance = toAccount.getAccountBalance();

                // call the transfer method to transfer the money
                fromAccount.transfer(toAccount, amount);

                // only continue if the money was actually transferred
                if (fromAccount.getAccountBalance() != previousFromBalance) {

                    fileHandling.saveUser(customer);

                    // save the successful transfer in the customer's transaction history
                    fileHandling.saveTransaction(customer, "Transfer Between Own Accounts", amount, fromAccount.getIban(), toAccount.getIban(), fromAccount.getAccountBalance());

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

            } else {
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

                        // check that the account has a debit card
                        if (fromAccount.getDebitCard() == null) {
                            System.out.println(Account.redBold + "\nYou need a debit card before making a transfer." + Account.textReset);
                            return;
                        }

                        // ask how much money they want to transfer
                        System.out.print("Enter amount to transfer: $");
                        double amount = scanner.nextDouble();

                        // get the total amount already transferred to other customers today
                        double todaysTransfers = fileHandling.getTodaysTransactionTotal(customer, fromAccount.getIban(), "Transfer To Another Customer");

                        // add this transfer to the amount already transferred today
                        double totalTransfers = todaysTransfers + amount;

                        // check if the new total would go over the cards daily transfer limit
                        if (totalTransfers > fromAccount.getDebitCard().getDailyTransferLimit()) {
                            System.out.println(Account.redBold + "\nTransfer exceeds your debit card daily transfer limit." + Account.textReset);
                            System.out.println("Daily Transfer Limit: $" + fromAccount.getDebitCard().getDailyTransferLimit());
                            System.out.println("Already Transferred Today: $" + todaysTransfers);
                            return;
                        }

                        // save my balance before the transfer
                        double previousBalance = fromAccount.getAccountBalance();
                        // transfer money from my account to the other customers checking account
                        fromAccount.transfer(destinationAccount, amount);


                        // only continue if the money was actually transferred
                        if (fromAccount.getAccountBalance() != previousBalance) {
                            // save both customers because both account balances changed
                            fileHandling.saveUser(customer);
                            fileHandling.saveUser(destinationCustomer);

                            // save the successful transfer in the senders transaction history
                            fileHandling.saveTransaction(customer, "Transfer To Another Customer", amount, fromAccount.getIban(), destinationCustomer.getId(), fromAccount.getAccountBalance());

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


    public void viewTransactionHistory(Customer customer) {
        // get the customers saved transactions from their file
        ArrayList<String> transactions = fileHandling.loadTransactions(customer);

        // create another list that will hold the transactions after filtering
        ArrayList<String> filteredTransactions = new ArrayList<>(transactions);

        // check if the customer has no transaction history
        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions found.");
            return;
        }

        // transaction history heading
        System.out.println("\n==================== Transaction History ====================");

        // show which customer the transaction history belongs to
        System.out.println("\nCustomer: " + customer.getName());
        System.out.println("Customer ID: " + customer.getId());

        // ask the customer how they want to filter their transactions
        System.out.println("\n==================== Filter Transactions ====================");
        System.out.println("1. All Transactions");
        System.out.println("2. Today");
        System.out.println("3. Yesterday");
        System.out.println("4. Last 7 Days");
        System.out.println("5. Last Week");
        System.out.println("6. Last 30 Days");
        System.out.println("7. Last Month");
        System.out.println("8. Specific Date");

        System.out.print("Choose a filter: ");
        int filterChoice = scanner.nextInt();

        LocalDate specificDate = null;

        // if the customer chose Specific Date
        if (filterChoice == 8) {

            System.out.print("Enter date (YYYY-MM-DD): ");
            String enteredDate = scanner.next();

            // convert the date entered by the customer from String to LocalDate
            specificDate = LocalDate.parse(enteredDate);
        }

        // get today's date
        LocalDate today = LocalDate.now();

        // create final variable so it can be used inside the lambda
        LocalDate finalSpecificDate = specificDate;

        // use a lambda to filter the transactions based on the customers choice
        // go through every transaction and remove it if it doesn't match the filter I want
        filteredTransactions.removeIf(transaction -> {
            // remove "Transaction=" from the beginning
            String transactionInfo = transaction.substring("Transaction=".length());

            // split the transaction into separate pieces wherever there is a comma
            String[] transactionData = transactionInfo.split(",");

            // get only the date so we can use it for filtering
            LocalDate transactionDate = LocalDate.parse(transactionData[0].substring(0, 10));

            // if the customer chose Today
            if (filterChoice == 2) {
                // remove transactions that were not made today
                return !transactionDate.equals(today);
            }

            // if the customer chose Yesterday
            if (filterChoice == 3) {

                LocalDate yesterday = today.minusDays(1);
                // remove transactions that were not made yesterday
                return !transactionDate.equals(yesterday);
            }

            // if the customer chose Last 7 Days
            if (filterChoice == 4) {

                LocalDate sevenDaysAgo = today.minusDays(6);
                // remove transactions that are older than the last 7 days
                return transactionDate.isBefore(sevenDaysAgo);
            }

            // if the customer chose Last Week
            if (filterChoice == 5) {

                // Last Week means the previous calendar week: Monday to Sunday
                // Example: if today is Friday Sep 18
                // this week = Monday Sep 14 to Sunday Sep 20
                // last week = Monday Sep 7 to Sunday Sep 13

                // find Monday of the current week
                LocalDate thisMonday = today.with(DayOfWeek.MONDAY);

                // go back one week to find last Monday
                LocalDate lastMonday = thisMonday.minusWeeks(1);

                // one day before this Monday gives us last Sunday
                LocalDate lastSunday = thisMonday.minusDays(1);

                // remove transactions outside last Monday to last Sunday
                return transactionDate.isBefore(lastMonday) || transactionDate.isAfter(lastSunday);
            }

            // if the customer chose Last 30 Days
            if (filterChoice == 6) {

                // Last 30 Days means 30 days including today
                // Example: if today is Sep 17
                // last 30 days = Aug 19 to Sep 17

                LocalDate thirtyDaysAgo = today.minusDays(29);

                // remove transactions older than the last 30 days
                return transactionDate.isBefore(thirtyDaysAgo);
            }

            // if the customer chose Last Month
            if (filterChoice == 7) {

                // Last Month means the previous calendar month
                // Example: if today is Sep 17
                // this month = September
                // last month = August 1 to August 31

                // Keep the year and month, but change the day to 1
                LocalDate firstDayThisMonth = today.withDayOfMonth(1);

                // go back one month to get the first day of last month
                LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);

                // one day before this month started = last day of last month
                LocalDate lastDayLastMonth = firstDayThisMonth.minusDays(1);

                // remove transactions that are not from last month
                return transactionDate.isBefore(firstDayLastMonth) || transactionDate.isAfter(lastDayLastMonth);
            }

            // if the customer chose Specific Date
            if (filterChoice == 8) {

                // remove transactions that do not match the date entered
                return !transactionDate.equals(finalSpecificDate);
            }

            // false means do not remove the transaction
            // this is used for option 1: All Transactions
            return false;
        });

        // go through every transaction left after filtering
        for (String transaction : filteredTransactions) {

            // remove "Transaction=" from the beginning
            String transactionInfo = transaction.substring("Transaction=".length());

            // split the transaction into separate pieces wherever there is a comma
            String[] transactionData = transactionInfo.split(",");

            // start with 2026-09-16T14:53:40.066862 and 16 only keeps 2026-09-16T14:53
            String dateTime = transactionData[0].substring(0, 16).replace("T", " ");
            String transactionType = transactionData[1];
            String amount = transactionData[2];
            String from = transactionData[3];
            String balanceAfter;


            System.out.println("\n-------------------------------------------------------------");
            // display the basic transaction info
            System.out.println("Date & Time:   " + dateTime);
            System.out.println("Type:          " + transactionType);
            System.out.printf("Amount:        $%.2f%n", Double.parseDouble(amount));

            // deposit and withdrawal only use one account that we deposit and withdraw from
            if (transactionType.equals("Deposit") || transactionType.equals("Withdrawal")) {
                System.out.println("Account:       " + from);

                // for deposit/withdrawal, balance is stored at index 4
                // example: Date,Type,Amount,Account,Balance
                // check where the balance is stored
                if (transactionData[4].isEmpty()) {
                    balanceAfter = transactionData[5];
                } else {
                    balanceAfter = transactionData[4];
                }

            } else {
                // transfers have a source and a destination
                // we need to display the destination info
                String destination = transactionData[4];
                System.out.println("From:          " + from);
                System.out.println("To:            " + destination);

                // for transfers, balance is stored at index 5
                // Date,Type,Amount,From,Destination,Balance
                balanceAfter = transactionData[5];
            }

            // show the balance after the transaction was completed
            System.out.printf("Balance After: $%.2f%n", Double.parseDouble(balanceAfter));
        }
    }


    // view account statement method
    public void viewAccountStatement(Customer customer) {
        System.out.println("\n==================== Account Statement ====================");

        // gets the customers list of accounts
        // take one Account at a time from that list and temporarily call it account
        // show all accounts that belong to this customer
        for (Account account : customer.getAccounts()) {
            System.out.println(account.getIban() + " - " + account.getAccountType());
        }

        // ask which account statement the customer wants
        System.out.println("\nEnter the IBAN of the account:");
        String iban = scanner.next();

        // search through that customers account for the matching iban
        // if exists then the Optional contains the account
        Optional<Account> selectedAccount = customer.getAccount(iban);

        // optional empty? check if the account was not found
        if (selectedAccount.isEmpty()) {
            System.out.println("Account not found.");
            return;
        }

        // get the account from the Optional
        Account account = selectedAccount.get();

        // display the selected account info
        System.out.println("\nAccount Type:    " + account.getAccountType());
        System.out.println("IBAN:            " + account.getIban());
        System.out.printf("Total Amount:    $%.2f%n", account.getAccountBalance());

        // get all transactions that belong to this customer
        ArrayList<String> transactions = fileHandling.loadTransactions(customer);

        // go through each transaction
        for (String transaction : transactions) {
            // remove "Transaction=" from the beginning
            String transactionInfo = transaction.substring("Transaction=".length());

            // split the transaction into separate pieces
            String[] transactionData = transactionInfo.split(",");

            // get the account iban used in this transaction
            // example: Date,Type,Amount,Account,Balance
            String transactionAccount = transactionData[3];

            // the customer can have more than one account
            // check if the selected account sent the money
            boolean sentMoney = transactionAccount.equals(account.getIban());

            // check if the selected account received money from their other account
            boolean receivedMoney = transactionData[1].equals("Transfer Between Own Accounts")
                    && transactionData[4].equals(account.getIban());

            // show the transaction if the selected account sent or received money
            if (sentMoney || receivedMoney) {
                // get the transaction details
                String dateTime = transactionData[0].substring(0, 16).replace("T", " ");
                String transactionType = transactionData[1];
                String amount = transactionData[2];

                // display the transaction in the account statement
                System.out.println("\n-------------------------------------------------------------");
                System.out.println("Date & Time:   " + dateTime);
                System.out.println("Type:          " + transactionType);

                if (receivedMoney) {
                    System.out.println("Amount:        +$" + amount);
                } else {
                    System.out.println("Amount:        -$" + amount);
                }
            }
        }
    }


    public void debitCardMenu(Customer customer) {
        System.out.println("\n==================== Debit Card ====================");

        // gets the customers list of accounts
        // take one Account at a time from that list and temporarily call it account
        // show all accounts that belong to this customer
        for (Account account : customer.getAccounts()) {
            System.out.println(account.getIban() + " - " + account.getAccountType());
        }

        // ask which account the customer wants to manage a debit card for
        System.out.println("\nEnter the IBAN of the account:");
        String iban = scanner.next();

        // search through that customers account for the matching iban
        // if exists then the Optional contains the account
        Optional<Account> selectedAccount = customer.getAccount(iban);

        // optional empty? check if the account was not found
        if (selectedAccount.isEmpty()) {
            System.out.println("Account not found.");
            return;
        }

        // get the account from the Optional
        Account account = selectedAccount.get();

        // check if this account already has a debit card
        if (account.getDebitCard() != null) {
            System.out.println("\nThis account already has a debit card.");
            System.out.println("Card Type: " + account.getDebitCard().getCardType());
            return;
        }

        // this account has no debit card yet, so let the customer choose one
        System.out.println("\nChoose a Debit Card:");
        System.out.println("1. Mastercard");
        System.out.println("2. Mastercard Titanium");
        System.out.println("3. Mastercard Platinum");

        System.out.println("\nEnter your choice:");
        int cardChoice = scanner.nextInt();

        // store the card type based on the customers choice
        String cardType;

        if (cardChoice == 1) {
            cardType = "Mastercard";

        } else if (cardChoice == 2) {
            cardType = "Mastercard Titanium";

        } else if (cardChoice == 3) {
            cardType = "Mastercard Platinum";

        } else {
            System.out.println("Invalid card choice.");
            return;
        }

        // create the debit card using the card type chosen by the customer
        DebitCard debitCard = new DebitCard(cardType);

        // connect the new debit card to the selected account
        account.setDebitCard(debitCard);

        // save the customer so the debit card is kept after restarting the program
        fileHandling.saveUser(customer);

        // confirm that the debit card was successfully added to the account
        System.out.println("\nDebit card added " + Account.greenBold + "successfully!" + Account.textReset);
        System.out.println("Card Type: " + debitCard.getCardType());
        System.out.println("Account:   " + account.getIban());
    }


    public void customerMenu(User user) {

        // keep showing the customer menu until the customer chooses to logout
        while (true) {

            System.out.println("\n==================== Customer Menu ====================");
            System.out.println("Welcome " + user.getName());

            System.out.println("1. View Accounts");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Account Statement");
            System.out.println("7. Debit Card");
            System.out.println("8. Logout");

            System.out.println("Choose an Option: ");

            int choice;

            try {
                // try to read the customers menu choice as a number
                choice = scanner.nextInt();

            } catch (InputMismatchException e) {

                // remove the invalid input from the scanner
                scanner.nextLine();

                System.out.println(Account.redBold + "Invalid input. Please enter a number from 1 to 8." + Account.textReset);

                // restart the while loop and show the customer menu again
                continue;
            }

            if (choice == 1) {
                viewAccounts((Customer) user);

            } else if (choice == 2) {
                depositMoney((Customer) user);

            } else if (choice == 3) {
                withdrawMoney((Customer) user);

            } else if (choice == 4) {
                transferMoney((Customer) user);

            } else if (choice == 5) {
                viewTransactionHistory((Customer) user);

            } else if (choice == 6) {
                viewAccountStatement((Customer) user);

            } else if (choice == 7) {
                debitCardMenu((Customer) user);

            } else if (choice == 8) {
                // stop the loop and logout the customer
                System.out.println("\nLogging out...");
                break;

            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
