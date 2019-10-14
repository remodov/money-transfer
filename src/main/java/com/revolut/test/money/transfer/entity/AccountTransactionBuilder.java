package com.revolut.test.money.transfer.entity;

import com.revolut.test.money.transfer.dto.TransactionStatus;

import java.math.BigDecimal;

public class AccountTransactionBuilder {
    private String accountFrom;
    private String accountTo;
    private TransactionStatus status;
    private BigDecimal amount;
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

    public AccountTransactionBuilder setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public AccountTransaction createAccountTransaction() {
        return new AccountTransaction(accountFrom, accountTo, status, amount, transactionId);
    }
}