package com.ga.project1.accounts;

// interface saying any class that implements TransactionActions must provide these three methods
// it defines the transaction actions an account must support.

// Account basically implements the interface and provides the actual methods logic
public interface iTransactionActions {
    double deposit(double amount);
    double withdraw(double amount);
    double transfer(Account destination, double amount);
}

