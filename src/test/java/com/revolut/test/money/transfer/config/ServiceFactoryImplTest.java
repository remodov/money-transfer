package com.revolut.test.money.transfer.config;

import org.junit.Assert;
import org.junit.Test;

public class ServiceFactoryImplTest {
    private ServiceFactory serviceFactory = new ServiceFactoryImpl();

    @Test
    public void createValidateTransactionRequestServiceReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createValidateTransactionRequestService());
    }

    @Test
    public void createAccountBalanceRepositoryReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createAccountBalanceRepository());
    }

    @Test
    public void createAccountLockServiceReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createAccountLockService());
    }

    @Test
    public void createTransferMoneyServiceReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createTransferMoneyService());
    }

    @Test
    public void createTransactionRepositoryReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createTransactionRepository());
    }

    @Test
    public void createTransactionServiceReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createTransactionService());
    }

    @Test
    public void createConverterReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createConverter());
    }

    @Test
    public void createObjectMapperReturnNotNullSuccess() {
        Assert.assertNotNull(serviceFactory.createObjectMapper());
    }
}