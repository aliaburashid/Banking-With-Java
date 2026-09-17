# ACME Bank - Banking with Java

A command-line banking application built in Java for Project 1. The application demonstrates object-oriented programming, file handling, authentication, banking transactions, debit-card limits, transaction history, exception handling, lambdas, Optionals, and unit testing.

## Project Overview

ACME Bank allows two types of users to interact with the banking system: **Customers** and **Bankers**.

Customers can create and access bank accounts, deposit and withdraw money, transfer money, view transaction history and account statements, and add a debit card. Bankers can log in, create new customers, and view customer details.

User, account, debit-card, login-security, and transaction information is persisted using text files so information remains available after restarting the program.

## Technologies Used

- Java 17
- IntelliJ IDEA
- Git and GitHub
- JUnit
- Java File Handling (`File`, `FileWriter`, `Scanner`)
- Java `Optional`
- Java Streams and Lambda Expressions
- `LocalDate` and `LocalDateTime`
- SHA-256 password hashing with `MessageDigest`

## Main Features

### Authentication

- Login using a user ID and password.
- Recognises whether the logged-in user is a Customer or Banker.
- Passwords are hashed using SHA-256 before being stored.
- Three failed login attempts temporarily lock login for one minute.
- Customers created by a banker receive a temporary password and must change it after logging in.

### Customer Accounts

- Customers can have a Checking account, Saving account, or both.
- Each account has its own IBAN and balance.
- Customer and account information is saved to the customer's text file.

### Banking Transactions

Customers can:

- Deposit money.
- Withdraw money.
- Transfer between their own accounts.
- Transfer money to another customer's Checking account.

Transactions are stored inside the customer's file with their date, time, amount, account information, and resulting balance.

### Overdraft Protection

- A $35 overdraft fee is applied when a withdrawal causes an overdraft.
- A customer cannot withdraw more than $100 while their balance is zero or negative.
- The account is deactivated after two overdrafts.
- The account is reactivated when the balance is restored to zero or above.

### Debit Cards

Each account can have zero or one debit card. The available card types are:

- Mastercard
- Mastercard Titanium
- Mastercard Platinum

Each card type has different daily withdrawal and transfer limits. The application calculates the transactions already completed that day before allowing another transaction.

### Transaction History

Customers can view their saved transaction history and filter it by:

- All transactions
- Today
- Yesterday
- Last 7 days
- Last week
- Last 30 days
- Last month
- Specific date

The filtering uses a lambda expression with `removeIf()`.

### Account Statement

Customers can select one of their accounts and view:

- Account type
- IBAN
- Current total balance
- Related transactions
- Transaction date and time
- Money entering or leaving the account

### Banker Features

Bankers can:

- Log in using their banker credentials.
- Add a new customer.
- Choose which account type to create for the customer.
- Generate a temporary password for the new customer.
- View an existing customer's details and accounts.
- Log out and return through the menu flow.

## Object-Oriented Programming

The project uses OOP concepts covered during the course.

### Abstraction and Inheritance

`User` is an abstract class containing information shared by both types of users. `Customer` and `Banker` extend `User`.

### Interface

`iTransactionActions` defines the banking actions an account must support:

```java
double deposit(double amount);
double withdraw(double amount);
double transfer(Account destination, double amount);
```

`Account` implements this interface and provides the transaction logic.

### Encapsulation

Class fields are kept private and accessed using methods such as getters and setters where required.

### Optional, Streams and Lambdas

`Customer.getAccount()` uses a Stream, lambda expression and `Optional<Account>` to search for an account safely:

```java
return accounts.stream()
        .filter(account -> account.getIban().equals(iban))
        .findFirst();
```

Lambda expressions are also used when filtering transaction history.

## File Handling

The application stores users in:

```text
data/customers/
data/bankers/
```

Files follow the naming structure:

```text
Customer-<CustomerName>-<CustomerID>.txt
Banker-<BankerName>-<BankerID>.txt
```

Customer files contain personal information, accounts, debit cards, login-security information, and transaction records.

`FileHandling` is responsible for saving and loading users, generating IDs, storing transactions, loading transaction history, and calculating daily transaction totals.

## Unit Testing

The `Account` banking logic is tested using JUnit. Tests cover important behaviours including:

- Depositing money.
- Withdrawing money.
- Applying the $35 overdraft fee.
- Transferring money between accounts.
- Deactivating an account after two overdrafts.
- Rejecting a zero withdrawal.
- Confirming the destination account receives transferred money.
- Reactivating an account after its negative balance is resolved.

All eight implemented Account tests pass.

## Project Structure

```text
BankingJavaCLI/
├── src/
│   └── com/ga/project1/
│       ├── Main.java
│       ├── FileHandling.java
│       ├── authentication/
│       │   └── Authentication.java
│       ├── accounts/
│       │   ├── Account.java
│       │   ├── DebitCard.java
│       │   └── iTransactionActions.java
│       ├── menu/
│       │   └── WelcomingMenu.java
│       └── users/
│           ├── User.java
│           ├── Customer.java
│           └── Banker.java
├── test/
│   └── com/ga/project1/
│       └── AccountTest.java
├── data/
│   ├── customers/
│   └── bankers/
└── README.md
```

## ERD

The Entity Relationship Diagram represents the main structure of the application:

- `User` is an abstract parent of `Customer` and `Banker`.
- A Customer can have one or two Accounts.
- An Account can have zero or one DebitCard.
- `Account` implements the `iTransactionActions` interface.
- `Authentication` handles password hashing and verification.
- `FileHandling` manages persistent data and transaction records.
- `WelcomingMenu` controls the command-line menus and connects the application's functionality.
- `Main` starts the application.

[View the Banking System ERD](https://lucid.app/lucidchart/3d005848-f3d4-4533-b486-1618a603d109/edit?viewport_loc=-1830%2C-6986%2C6371%2C3970%2CHWEp-vi-RSFO&invitationId=inv_71b05347-744d-46d3-a4e2-3211e8db3bca)


## Planning

The project was broken down into individual banking requirements and implemented incrementally. Features were tested as they were added before moving to the next requirement.

Planning included:

- Designing the main classes and relationships.
- Creating customer and banker authentication.
- Building account and transaction functionality.
- Adding file persistence.
- Implementing overdraft rules.
- Adding transaction history and filtering.
- Adding debit cards and daily limits.
- Adding fraud-detection login lockout.
- Writing unit tests.
- Refactoring repeated code and completing documentation.

### Trello
Trello: [Banking with Java CLI](https://trello.com/b/te8PsAmC/banking-with-java-cli)


## Favourite Functions

### `getAccount()`

## Favourite Part

My favourite part of the project was implementing the temporary password feature.

When a banker creates a new customer, the system generates a temporary password and stores its hashed version rather than the plain password. When the customer logs in using the temporary password, they are required to create a new password for security purposes.

The new password is stored in a variable, hashed using SHA-256, and then replaces the previous password hash. Finally, `temporaryPassword` is changed to `false` and the updated customer is saved.

I enjoyed this feature because it helped me understand how authentication, password hashing, objects, and file handling can work together in one process.


## Challenges and Unresolved Issues

One challenge was keeping transaction history when customer files were updated. Since `saveUser()` rewrites the customer file, the existing transaction lines first had to be read and then written back so they were not lost.

Another challenge was implementing overdraft persistence. Overdraft count, fees and account status had to be saved so that restarting the application did not reset a deactivated account.

The distinction between the specified daily **Deposit Limit** and **Deposit Own Account Limit** was not fully clear for cash deposits, so this remains an area for further clarification and development.

## Future Improvements

Possible future improvements include:

- Stronger production password hashing using a password-specific algorithm and salt.
- More input validation throughout the CLI.
- Moving from text-file persistence to a relational database.
- Adding more unit and integration tests.
- Expanding transaction filtering to support more precise date-and-time searches.
- Developing a graphical or web-based interface.

## How to Run

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Make sure Java 17 is configured as the project SDK.
4. Run `Main.java`.
5. Use the command-line menu to sign up or log in.

## References

The following documentation was used as learning/reference material during development:

- Oracle Java Documentation — `File`
- Oracle Java Documentation — `FileWriter`
- Oracle Java Documentation — `Scanner`
- Baeldung — SHA-256 Hashing in Java

These resources were used as references for understanding the Java APIs and techniques used in the project.
