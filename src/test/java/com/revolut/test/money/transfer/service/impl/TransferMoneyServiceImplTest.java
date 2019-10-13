package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import com.revolut.test.money.transfer.test.utils.TestDataFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TransferMoneyServiceImplTest {
    private AccountBalanceRepository accountBalanceRepositoryMock = Mockito.mock(AccountBalanceRepository.class);
    private AccountLockService accountLockServiceMock = new AccountLocksServiceSimple(accountBalanceRepositoryMock);

    private TransferMoneyService transferMoneyServiceMock =
            new TransferMoneyServiceImpl(accountBalanceRepositoryMock, accountLockServiceMock);

    @Test
    public void transferMoneyWithPositiveBalanceSuccess() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",200D);
        AccountBalance accountTo = new AccountBalance("12345678912345678902",300D);

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();

        transferMoneyServiceMock.transfer(transactionTransferRequest);

        Mockito.verify(accountBalanceRepositoryMock, Mockito.times(1))
                .updateAccountBalance(new AccountBalance("12345678912345678901",189.89D),
                                      new AccountBalance("12345678912345678902",310.11D));
    }

    @Test(expected = MoneyTransferException.class)
    public void transferMoneyWithNegativeBalanceAccountFromThrowsMoneyTransferException() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",-200D);
        AccountBalance accountTo = new AccountBalance("12345678912345678902",300D);

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();

        transferMoneyServiceMock.transfer(transactionTransferRequest);
    }

    @Test(expected = MoneyTransferException.class)
    public void transferMoneyNotFoundAccountToBalanceThrowsMoneyTransferException() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",200D);
        AccountBalance accountTo = new AccountBalance("12345678912345678902",300D);

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.empty());

        accountLockServiceMock.initAccountLocks();

        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();

        transferMoneyServiceMock.transfer(transactionTransferRequest);
    }

    @Test(expected = MoneyTransferException.class)
    public void transferMoneyNotFoundAccountFromBalanceThrowsMoneyTransferException() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",200D);
        AccountBalance accountTo = new AccountBalance("12345678912345678902",300D);

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.empty());

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();

        transferMoneyServiceMock.transfer(transactionTransferRequest);
    }

    @Test
    public void transferMoneyFromAccountAndToAccountDeadLock() throws InterruptedException {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901", 20000D);
        AccountBalance accountTo = new AccountBalance("12345678912345678902", 30000D);

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferRequest transactionTransferRequestFromTo =
                TestDataFactory.createTransactionTransferRequest();
        transactionTransferRequestFromTo.setAmount(1D);

        TransactionTransferRequest transactionTransferRequestToFrom =
                TestDataFactory.createTransactionTransferRequest();
        transactionTransferRequestToFrom.setAccountFrom(transactionTransferRequestFromTo.getAccountTo());
        transactionTransferRequestToFrom.setAccountTo(transactionTransferRequestFromTo.getAccountFrom());
        transactionTransferRequestToFrom.setAmount(1D);

        CountDownLatch countDownLatch = new CountDownLatch(5_000);

        new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted()) {
                transferMoneyServiceMock.transfer(transactionTransferRequestFromTo);
                countDownLatch.countDown();
            }
        }
        ).start();

        new Thread(() ->
        {
            while (!Thread.currentThread().isInterrupted()) {
                transferMoneyServiceMock.transfer(transactionTransferRequestToFrom);
                countDownLatch.countDown();
            }
        }).start();

        boolean isNoDeadLock = countDownLatch.await(1, TimeUnit.MINUTES);

        Assert.assertTrue(isNoDeadLock);
    }
}