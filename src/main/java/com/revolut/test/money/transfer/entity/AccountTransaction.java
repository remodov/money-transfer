package com.revolut.test.money.transfer.entity;

import com.revolut.test.money.transfer.dto.TransactionStatus;

import java.math.BigDecimal;

public class AccountTransaction {
    private Long transactionId;
    private String accountFrom;
    private String accountTo;
    private TransactionStatus status;
    private BigDecimal amount;

    public AccountTransaction(String accountFrom,
                              String accountTo,
                              TransactionStatus status,
                              BigDecimal amount,
                              Long transactionId
    ) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.status = status;
        this.amount = amount;
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AccountTransaction{" +
                "transactionId=" + transactionId +
                ", accountFrom='" + accountFrom + '\'' +
                ", accountTo='" + accountTo + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                '}';
    }
}
