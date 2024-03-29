package com.revolut.test.money.transfer.service;

import com.revolut.test.money.transfer.dto.TransactionTransferResponse;

/**
 * Service change account balance
 */
public interface TransferMoneyService {
    /**
     * Transfer money from account to account on amount sum
     */
    void transfer(TransactionTransferResponse transactionTransferRequest);
}
