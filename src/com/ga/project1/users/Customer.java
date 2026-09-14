package com.ga.project1.users;

import com.ga.project1.accounts.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Customer extends User {

    private String email;
    private String address;
    private String phoneNumber;
    private List<Account> accounts;

    public Customer(String id, String name, String hashedPassword,
                    String email, String address, String phoneNumber) {

        super(id, name, hashedPassword);
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accounts = new ArrayList<>(); // every customer starts with an empty list of accounts
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    // find one particular account
    public Optional<Account> getAccount(String iban) {
        return accounts.stream()
                .filter(account -> account.getIban().equals(iban))
                .findFirst();
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // give me all the accounts
    public List<Account> getAccounts() {
        return accounts;
    }
}