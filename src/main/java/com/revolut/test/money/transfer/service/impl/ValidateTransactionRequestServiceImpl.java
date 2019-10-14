package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.service.ValidateTransactionRequestService;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class ValidateTransactionRequestServiceImpl implements ValidateTransactionRequestService {
    public Optional<String> validate(TransactionTransferRequest transactionTransferRequest) {
        if (transactionTransferRequest.getAmount() == null || transactionTransferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of("Wrong amount value, must be more then zero");
        }

        if (transactionTransferRequest.getAccountTo() == null || transactionTransferRequest.getAccountTo().length() != 20) {
            return Optional.of("Wrong account to value, length must be 20");
        }

        if (transactionTransferRequest.getAccountFrom() == null || transactionTransferRequest.getAccountFrom().length() != 20) {
            return Optional.of("Wrong account from value, length must be 20");
        }

        if (Objects.equals(transactionTransferRequest.getAccountFrom(), transactionTransferRequest.getAccountTo())) {
            return Optional.of("Account from could not equals account to");
        }

        return Optional.empty();
    }
}
