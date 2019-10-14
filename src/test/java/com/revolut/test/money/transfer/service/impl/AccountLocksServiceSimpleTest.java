package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;

public class AccountLocksServiceSimpleTest {
    @Test
    public void getLoadedAccountLockSuccess() {
        AccountBalanceRepository accountBalanceRepositoryMock = Mockito.mock(AccountBalanceRepository.class);

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
               .thenReturn(Arrays.asList(new AccountBalance("12345678912345678901",new BigDecimal(200)),
                                         new AccountBalance("12345678912345678902",new BigDecimal(300))));

        AccountLockService accountLockService =
                new AccountLocksServiceSimple(accountBalanceRepositoryMock);

        accountLockService.initAccountLocks();

        Assert.assertNotNull(accountLockService.getAccountLock("12345678912345678901"));
    }

    @Test(expected = MoneyTransferException.class)
    public void getNotLoadedAccountLockThrowsMoneyTransferException() {
        AccountBalanceRepository accountBalanceRepositoryMock = Mockito.mock(AccountBalanceRepository.class);

        AccountLockService accountLockService =
                new AccountLocksServiceSimple(accountBalanceRepositoryMock);

        Assert.assertNotNull(accountLockService.getAccountLock("12345678912345678903"));
    }
}