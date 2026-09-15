package com.ga.project1.accounts;

public class Account {
    private String iban;
    private double accountBalance;
    private String accountType;
    private int overdraftCount;
    private double overdraftFees;
    private boolean active;
    private static final double OVERDRAFT_FEE = 35.0;
    public static String textReset = "\u001B[0m";
    public static String redBold = "\u001B[1;31m";
    public static String greenBold = "\u001B[1;32m";


    public Account(String accountIban, double accountBalance, String accountType) {
        this.iban = accountIban;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
        overdraftCount = 0;
        overdraftFees = 0;
        this.active = true;
    }


    public double deposit(double amount) {
        accountBalance = accountBalance + amount;
        // reactivate account once negative balance and fees resolved
        if (!active && accountBalance >= 0 ) {
            active = true;
            overdraftCount = 0;
            overdraftFees = 0;
            System.out.println("\nYour account has been " + greenBold + "REACTIVATED" + textReset + ".");
        }
        return accountBalance;
    }


    public double withdraw(double amount) {

        // validation: preventing negative or 0 transaction amount
        if (amount <= 0) {
            System.out.println("\nWithdrawal amount must be greater than $0.");
            return accountBalance;
        }

        // validation: preventing withdrawal from a deactivated account
        if (!active) {
            System.out.println( redBold + "\nAlert:" + textReset + " Overdraft limit reached. Your account is " + redBold + "DEACTIVATED" + textReset + ".");
            return accountBalance;
        }

        // validation: preventing withdrawing more than $100 while account balance is negative
        if (accountBalance <= 0 && amount > 100) {
            System.out.println( redBold + "\nTRANSACTION CAN'T BE COMPLETED! \nYou cannot withdraw more than $100 while your balance is 0 or negative :(" + textReset);
            return accountBalance;
        }

        // actual withdraw function
        if (accountBalance >= amount) {
            accountBalance = accountBalance - amount;

        } else {
            // if account balance < amount, overdraft happens
            accountBalance = accountBalance - amount;
            accountBalance = accountBalance - OVERDRAFT_FEE;
            overdraftFees = overdraftFees + OVERDRAFT_FEE;
            overdraftCount++;
            System.out.println("\n-------------------------------------------------------");
            System.out.println("OVERDRAFT WARNING!");
            System.out.println("-------------------------------------------------------");
            System.out.println(redBold + "Alert " + textReset + ": Running on borrowed funds!");
            System.out.println(redBold +"Overdraft Fee:"  + textReset + " -$" + OVERDRAFT_FEE);
            if (overdraftCount == 1) {
                System.out.println(redBold + "Alert" + textReset + " : You have" + redBold + " 1" + textReset + " overdraft remaining before your account is deactivated.");
            } else if (overdraftCount >= 2) {
                active = false;
                System.out.println("Alert: Overdraft limit reached. Your account is " + redBold + "DEACTIVATED" + textReset + " due to multiple overdrafts." );
            }
            System.out.println("-------------------------------------------------------");
        }
        return accountBalance;
    }

    public double transfer(Account destination, double amount) {
        // take the money from this account
        accountBalance = accountBalance - amount;
        // put the money into this account
        destination.accountBalance = destination.accountBalance + amount;
        System.out.println("Amount of $" + amount + " successfully transferred!");
        return accountBalance;
    }

    // due to private iban, customer can't directly access it.
    public String getIban() {
        return iban;
    }

    // due to private balance, customer can't directly access it.
    public double getAccountBalance() {
        return accountBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public int getOverdraftCount() {
        return overdraftCount;
    }

    public double getOverdraftFees() {
        return overdraftFees;
    }

    public boolean isActive() {
        return active;
    }

    public void setOverdraftCount(int overdraftCount) {
        this.overdraftCount = overdraftCount;
    }

    public void setOverdraftFees(double overdraftFees) {
        this.overdraftFees = overdraftFees;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
