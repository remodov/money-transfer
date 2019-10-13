package com.revolut.test.money.transfer.service;

import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;

import java.util.Optional;

/**
 * Service for work with transactions.
 * Simple CRUD for save and search.
 */
public interface TransactionService {
    /**
     * Init transaction.
     */
    TransactionTransferResponse initTransaction(TransactionTransferRequest request);

    /**
     * Change transaction status.
     */
    void updateStatus(Long transactionID, TransactionStatus transactionStatus);

    Optional<TransactionTransferResponse> findById(Long transactionID);
}
