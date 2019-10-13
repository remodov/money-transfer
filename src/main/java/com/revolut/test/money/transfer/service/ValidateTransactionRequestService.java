package com.revolut.test.money.transfer.service;

import com.revolut.test.money.transfer.dto.TransactionTransferRequest;

import java.util.Optional;

/**
 * Validate http request body (TransactionTransferRequest)
 */
public interface ValidateTransactionRequestService {
    Optional<String> validate(TransactionTransferRequest transactionTransferRequest);
}
