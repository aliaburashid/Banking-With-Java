package com.ga.project1;
import com.ga.project1.accounts.Account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

public class AccountTest {

    // account object used for each test
    Account account;

    // create a new account before every test
    // this makes sure every test starts with the same balance
    @Before
    public void setUp() {
        account = new Account("BH00ACME00000000000001", 500.0, "Checking");
    }

    // test depositing money into the account
    @Test
    @DisplayName("When 100 is deposited then balance becomes 600")
    public final void when100IsDepositedThenBalanceBecomes600() {
        // deposit $100 into an account that starts with $500
        account.deposit(100);
        // expected balance is $600 after the deposit
        // delta expected for junit4 form to compare doubles
        Assert.assertEquals(600.0, account.getAccountBalance(), 0.01);
    }

    // test withdrawing money from the account
    @Test
    @DisplayName("When 100 is withdrawn then balance becomes 400")
    public final void when100IsWithdrawnThenBalanceBecomes400() {

        // withdraw $100 from an account that starts with $500
        account.withdraw(100);

        // expected balance is $400 after the withdrawal
        Assert.assertEquals(400.0, account.getAccountBalance(), 0.01);
    }


    // test that the overdraft fee is charged when the withdrawal is more than the balance
    @Test
    @DisplayName("When withdrawal causes overdraft then 35 dollar fee is charged")
    public final void whenWithdrawalCausesOverdraftThenFeeIsCharged() {

        // withdraw $600 from an account that starts with $500
        account.withdraw(600);

        // expected balance is -135 after the $35 overdraft fee
        Assert.assertEquals(-135.0, account.getAccountBalance(), 0.01);
    }


    // test transferring money from one account to another
    @Test
    @DisplayName("When 100 is transferred then source balance becomes 400")
    public final void when100IsTransferredThenSourceBalanceBecomes400() {

        // create another account to receive the money
        Account destination = new Account("BH00ACME00000000000002", 200.0, "Savings");

        // transfer $100 from the account to the destination account
        account.transfer(destination, 100);

        // expected source account balance is $400 after the transfer
        Assert.assertEquals(400.0, account.getAccountBalance(), 0.01);
    }

    // test that transferred money is added to the destination account
    // previous transfer test checked the source became $400
    // Now check that the destination actually receives the $100
    @Test
    @DisplayName("When 100 is transferred then destination balance increases by 100")
    public final void when100IsTransferredThenDestinationBalanceIncreasesBy100() {

        // create another account that starts with $200
        Account destination = new Account("BH00ACME00000000000002", 200.0, "Savings"
        );

        // transfer $100 to the destination account
        account.transfer(destination, 100);

        // destination account should now have $300
        Assert.assertEquals(300.0, destination.getAccountBalance(), 0.01);
    }


    // test that the account is deactivated after 2 overdrafts
    @Test
    @DisplayName("When account has 2 overdrafts then account is deactivated")
    public final void whenAccountHas2OverdraftsThenAccountIsDeactivated() {

        // first overdraft
        account.withdraw(600);

        // second overdraft while the account balance is negative
        account.withdraw(50);

        // account should be deactivated after the second overdraft
        Assert.assertFalse(account.isActive());
    }


    // test that a deactivated account is reactivated after the negative balance is resolved
    @Test
    @DisplayName("When negative balance is resolved then account is reactivated")
    public final void whenNegativeBalanceIsResolvedThenAccountIsReactivated() {

        // cause the first overdraft
        account.withdraw(600);

        // cause the second overdraft which deactivates the account
        account.withdraw(50);

        // deposit enough money to make the balance positive again
        account.deposit(300);

        // account should be active again
        Assert.assertTrue(account.isActive());
    }


    // test that withdrawing 0 does not change the account balance
    @Test
    @DisplayName("When 0 is withdrawn then balance stays the same")
    public final void when0IsWithdrawnThenBalanceStaysTheSame() {

        // try to withdraw $0 from an account that starts with $500
        account.withdraw(0);

        // balance should still be $500
        Assert.assertEquals(500.0, account.getAccountBalance(), 0.01);
    }
}
