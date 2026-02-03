package com.travelapp.exception;

public class InvalidExpenseAmountException extends BusinessException {
    public InvalidExpenseAmountException() {
        super("Expense amount must be greater than zero");
    }
}
