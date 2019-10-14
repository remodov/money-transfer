package com.revolut.test.money.transfer.service;

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

    Optional<TransactionTransferResponse> findById(Long transactionID);
}
