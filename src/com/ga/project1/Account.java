package com.ga.project1;

public class Account {
    private String iban;
    private double accountBalance;
    private String accountType;
    private int overdraftCount;
    private double overdraftFees;
    private boolean active;
    private static final double OVERDRAFT_FEE = 35.0;
    public String textReset;
    public String redBold;
    public String greenBold;


    public Account(String accountIban, double accountBalance, String accountType) {
        this.iban = accountIban;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
        overdraftCount = 0;
        overdraftFees = 0;
        this.active = true;
        this.textReset = "\u001B[0m";
        this.redBold = "\u001B[1;31m";
        this.greenBold = "\u001B[1;32m";
    }


    public double deposit(double amount) {
        accountBalance = accountBalance + amount;
        System.out.println("------------------------------------------------");
        System.out.println("Amount of $" + amount + " successfully deposited!");
        System.out.println("Current balance: $" + accountBalance);
        System.out.println("------------------------------------------------");

        // reactivate account once negative balance and fees resolved
        if (!active && accountBalance >= 0 ) {
            active = true;
            overdraftCount = 0;
            overdraftFees = 0;
            System.out.println("Your account has been " + greenBold + "REACTIVATED" + textReset + ".");
        }
        return accountBalance;
    }


    public double withdraw(double amount) {

        // validation: preventing negative or 0 transaction amount
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than $0.");
            return accountBalance;
        }

        // validation: preventing withdrawal from a deactivated account
        if (!active) {
            System.out.println("Alert: Overdraft limit reached. Your account is " + redBold + "DEACTIVATED" + textReset + ".");
            return accountBalance;
        }

        // validation: preventing withdrawing more than $100 while account balance is negative
        if (accountBalance < 0 && amount > 100) {
            System.out.println("TRANSACTION CAN'T BE COMPLETED! \nYou cannot withdraw more than $100 while your balance is negative :(");
            return accountBalance;
        }

        // actual withdraw function
        if (accountBalance >= amount) {
            accountBalance = accountBalance - amount;
            System.out.println("------------------------------------------------");
            System.out.println("Amount of $" + amount + " successfully withdrew");
            System.out.println("------------------------------------------------");
        } else {
            // if account balance < amount, overdraft happens
            accountBalance = accountBalance - amount;
            overdraftFees = overdraftFees + OVERDRAFT_FEE;
            overdraftCount++;
            System.out.println("Warning: Running on borrowed funds! A fee of $" + OVERDRAFT_FEE + " Has been applied.");
            System.out.println("Current Balance: " + accountBalance);

            // first overdraft warning and deactivate account
            if (overdraftCount == 1) {
                System.out.println("Alert: Remaining overdraft limit is '" + redBold + "1" + textReset + "' before your account is deactivated.");
            } else if (overdraftCount >= 2) {
                active = false;
                System.out.println("Alert: Overdraft limit reached. Your account is " + redBold + "DEACTIVATED" + textReset + ".");
            }
        }
        return accountBalance;

        }
    }
}
