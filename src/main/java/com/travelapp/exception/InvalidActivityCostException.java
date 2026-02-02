package com.travelapp.exception;

public class InvalidActivityCostException extends BusinessException {

    public InvalidActivityCostException() {
        super("Cost cannot be negative");
    }
}
