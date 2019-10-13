package com.revolut.test.money.transfer.entity;

import com.revolut.test.money.transfer.dto.TransactionStatus;

public class AccountTransactionBuilder {
    private String accountFrom;
    private String accountTo;
    private TransactionStatus status;
    private Double amount;
    private Long transactionId;

    public AccountTransactionBuilder setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public AccountTransactionBuilder setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
        return this;
    }

    public AccountTransactionBuilder setAccountTo(String accountTo) {
        this.accountTo = accountTo;
        return this;
    }

    public AccountTransactionBuilder setStatus(TransactionStatus status) {
        this.status = status;
        return this;
    }

    public AccountTransactionBuilder setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public AccountTransaction createAccountTransaction() {
        return new AccountTransaction(accountFrom, accountTo, status, amount, transactionId);
    }
}