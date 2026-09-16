package com.ga.project1.accounts;

public class DebitCard {

    private String cardType;

    private double dailyWithdrawLimit;
    private double dailyTransferLimit;
    private double dailyOwnTransferLimit;
    private double dailyDepositLimit;
    private double dailyOwnDepositLimit;

    public DebitCard(String cardType) {
        this.cardType = cardType;
    }

}
