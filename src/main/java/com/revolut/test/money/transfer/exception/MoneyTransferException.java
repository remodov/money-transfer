package com.revolut.test.money.transfer.exception;

public class MoneyTransferException extends RuntimeException {
    public MoneyTransferException(){}

    public MoneyTransferException(String message){
        super(message);
    }
}
