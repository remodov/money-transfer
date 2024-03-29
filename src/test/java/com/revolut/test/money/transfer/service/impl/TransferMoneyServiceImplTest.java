package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import com.revolut.test.money.transfer.test.utils.TestDataFactory;
import com.revolut.test.money.transfer.test.utils.TestDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.eq;

public class TransferMoneyServiceImplTest extends TestDatabase {
    private AccountBalanceRepository accountBalanceRepositoryMock = Mockito.mock(AccountBalanceRepository.class);
    private TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
    private AccountLockService accountLockServiceMock = new AccountLocksServiceSimple(accountBalanceRepositoryMock);

    private TransferMoneyService transferMoneyServiceMock =
            new TransferMoneyServiceImpl(accountBalanceRepositoryMock, transactionRepository, accountLockServiceMock);

    @Test
    public void transferMoneyWithPositiveBalanceSuccess() throws SQLException {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901", new BigDecimal(200));
        AccountBalance accountTo = new AccountBalance("12345678912345678902",new BigDecimal(300));

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferResponse transactionTransferRequest =
                TestDataFactory.createTransactionTransferResponse();

        transferMoneyServiceMock.transfer(transactionTransferRequest);

        Mockito.verify(accountBalanceRepositoryMock, Mockito.times(1))
                .update(eq(new AccountBalance("12345678912345678901", new BigDecimal("189.89"))), Mockito.anyObject());

        Mockito.verify(accountBalanceRepositoryMock, Mockito.times(1))
                .update(eq(new AccountBalance("12345678912345678902", new BigDecimal("310.11"))), Mockito.anyObject());

    }

    @Test(expected = MoneyTransferException.class)
    public void transferMoneyWithNegativeBalanceAccountFromThrowsMoneyTransferException() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",new BigDecimal(-200));
        AccountBalance accountTo = new AccountBalance("12345678912345678902",new BigDecimal(300));

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferResponse transactionTransferRequest =
                TestDataFactory.createTransactionTransferResponse();

        transferMoneyServiceMock.transfer(transactionTransferRequest);
    }

    @Test(expected = MoneyTransferException.class)
    public void transferMoneyNotFoundAccountToBalanceThrowsMoneyTransferException() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",new BigDecimal(200));
        AccountBalance accountTo = new AccountBalance("12345678912345678902",new BigDecimal(300));

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.empty());

        accountLockServiceMock.initAccountLocks();

        TransactionTransferResponse transactionTransferRequest =
                TestDataFactory.createTransactionTransferResponse();

        transferMoneyServiceMock.transfer(transactionTransferRequest);
    }

    @Test(expected = MoneyTransferException.class)
    public void transferMoneyNotFoundAccountFromBalanceThrowsMoneyTransferException() {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901",new BigDecimal(200));
        AccountBalance accountTo = new AccountBalance("12345678912345678902",new BigDecimal(300));

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.empty());

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferResponse transactionTransferRequest =
                TestDataFactory.createTransactionTransferResponse();

        transferMoneyServiceMock.transfer(transactionTransferRequest);
    }

    @Test
    public void transferMoneyFromAccountAndToAccountDeadLock() throws InterruptedException {
        AccountBalance accountFrom = new AccountBalance("12345678912345678901", new BigDecimal(20000));
        AccountBalance accountTo = new AccountBalance("12345678912345678902", new BigDecimal(30000));

        Mockito.when(accountBalanceRepositoryMock.findAllAccounts())
                .thenReturn(Arrays.asList(accountFrom, accountTo));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678901"))
                .thenReturn(Optional.of(accountFrom));

        Mockito.when(accountBalanceRepositoryMock.findAccountBalanceByAccountNo("12345678912345678902"))
                .thenReturn(Optional.of(accountTo));

        accountLockServiceMock.initAccountLocks();

        TransactionTransferResponse transactionTransferRequestFromTo =
                TestDataFactory.createTransactionTransferResponse();
        transactionTransferRequestFromTo.setAmount(new BigDecimal(1));

        TransactionTransferResponse transactionTransferRequestToFrom =
                TestDataFactory.createTransactionTransferResponse();
        transactionTransferRequestToFrom.setAccountFrom(transactionTransferRequestFromTo.getAccountTo());
        transactionTransferRequestToFrom.setAccountTo(transactionTransferRequestFromTo.getAccountFrom());
        transactionTransferRequestToFrom.setAmount(new BigDecimal(1));

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