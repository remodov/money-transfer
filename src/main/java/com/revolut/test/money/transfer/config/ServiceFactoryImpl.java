package com.revolut.test.money.transfer.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.money.transfer.converter.FromAccountTransactionToTransactionTransferResponseConverter;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.repository.impl.AccountBalanceRepositoryJdbcImpl;
import com.revolut.test.money.transfer.repository.impl.TransactionRepositoryJdbcImpl;
import com.revolut.test.money.transfer.service.AccountLockService;
import com.revolut.test.money.transfer.service.TransactionService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import com.revolut.test.money.transfer.service.impl.AccountLocksServiceSimple;
import com.revolut.test.money.transfer.service.impl.TransactionServiceImpl;
import com.revolut.test.money.transfer.service.impl.TransferMoneyServiceImpl;
import com.revolut.test.money.transfer.service.impl.ValidateTransactionRequestServiceImpl;

import java.util.function.Function;

public class ServiceFactoryImpl implements ServiceFactory {

    @Override
    public ValidateTransactionRequestServiceImpl createValidateTransactionRequestService() {
        return new ValidateTransactionRequestServiceImpl();
    }

    @Override
    public AccountBalanceRepository createAccountBalanceRepository() {
        return new AccountBalanceRepositoryJdbcImpl();
    }

    @Override
    public AccountLockService createAccountLockService() {
        AccountLocksServiceSimple accountLocksServiceSimple =
                new AccountLocksServiceSimple(createAccountBalanceRepository());
        accountLocksServiceSimple.initAccountLocks();
        return accountLocksServiceSimple;
    }

    @Override
    public TransferMoneyService createTransferMoneyService() {
        return new TransferMoneyServiceImpl(createAccountBalanceRepository(), createTransactionRepository(), createAccountLockService());
    }

    @Override
    public TransactionRepository createTransactionRepository(){
      return new TransactionRepositoryJdbcImpl();
    }

    @Override
    public TransactionService createTransactionService(){
        return new TransactionServiceImpl(createTransactionRepository(), createConverter());
    }

    @Override
    public Function<AccountTransaction, TransactionTransferResponse> createConverter() {
        return new FromAccountTransactionToTransactionTransferResponseConverter();
    }

    @Override
    public ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        return objectMapper;
    }
}
