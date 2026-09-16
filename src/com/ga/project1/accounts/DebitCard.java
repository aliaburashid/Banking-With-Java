package com.ga.project1.accounts;

public class DebitCard {

    private String cardType;

    private double dailyWithdrawLimit;
    private double dailyTransferLimit;
    private double dailyOwnTransferLimit;
    private double dailyDepositLimit;
    private double dailyOwnDepositLimit;

    public DebitCard(String cardType) {
        // save the type of debit card chosen by the customer
        this.cardType = cardType;

        // set the daily transaction limits for a standard Mastercard
        if (cardType.equals("Mastercard")) {
            dailyWithdrawLimit = 5000;
            dailyTransferLimit = 10000;
            dailyOwnTransferLimit = 20000;

            // Titanium has higher daily limits than the standard Mastercard
        } else if (cardType.equals("Mastercard Titanium")) {
            dailyWithdrawLimit = 10000;
            dailyTransferLimit = 20000;
            dailyOwnTransferLimit = 40000;
            // Platinum has the highest daily transaction limits

        } else if (cardType.equals("Mastercard Platinum")) {
            dailyWithdrawLimit = 20000;
            dailyTransferLimit = 40000;
            dailyOwnTransferLimit = 80000;
        }
    }

    // return the type of debit card the customer selected
    public String getCardType() {
        return cardType;
    }

    // return the maximum amount that can be withdrawn per day
    public double getDailyWithdrawLimit() {
        return dailyWithdrawLimit;
    }

    // return the maximum amount that can be transferred to another customer per day
    public double getDailyTransferLimit() {
        return dailyTransferLimit;
    }

    // return the maximum amount that can be transferred between the customers own accounts per day
    public double getDailyOwnTransferLimit() {
        return dailyOwnTransferLimit;
    }


}
