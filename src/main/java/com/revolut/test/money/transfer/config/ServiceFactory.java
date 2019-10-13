package com.revolut.test.money.transfer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import com.revolut.test.money.transfer.service.TransactionService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import com.revolut.test.money.transfer.service.impl.ValidateTransactionRequestServiceImpl;

import java.util.function.Function;

public interface ServiceFactory {
    ValidateTransactionRequestServiceImpl createValidateTransactionRequestService();

    AccountBalanceRepository createAccountBalanceRepository();

    AccountLockService createAccountLockService();

    TransferMoneyService createTransferMoneyService();

    TransactionRepository createTransactionRepository();

    TransactionService createTransactionService();

    Function<AccountTransaction, TransactionTransferResponse> createConverter();

    ObjectMapper createObjectMapper();
}
