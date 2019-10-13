package com.revolut.test.money.transfer.dto;

public class TransactionTransferResponse extends TransactionTransferRequest {
    private TransactionStatus transactionStatus;
    private Long transactionId;

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
