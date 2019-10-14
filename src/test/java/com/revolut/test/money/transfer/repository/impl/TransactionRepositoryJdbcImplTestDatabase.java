package com.revolut.test.money.transfer.repository.impl;

import com.revolut.test.money.transfer.config.DataSource;
import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.test.utils.TestDataFactory;
import com.revolut.test.money.transfer.test.utils.TestDatabase;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class TransactionRepositoryJdbcImplTestDatabase extends TestDatabase {
    private TransactionRepository transactionRepository = new TransactionRepositoryJdbcImpl();

    @Test
    public void saveAccountTransactionSuccess() {
        AccountTransaction accountTransaction = TestDataFactory.createAccountTransaction();

        AccountTransaction accountTransactionSaved =
                transactionRepository.save(accountTransaction);

        Assert.assertEquals(accountTransactionSaved.getAccountFrom(), accountTransaction.getAccountFrom());
        Assert.assertEquals(accountTransactionSaved.getAccountTo(), accountTransaction.getAccountTo());
        Assert.assertEquals(accountTransactionSaved.getAmount(), accountTransaction.getAmount());
        Assert.assertEquals(accountTransactionSaved.getStatus(), accountTransaction.getStatus());
        Assert.assertNotNull(accountTransactionSaved.getTransactionId());
    }

    @Test(expected = MoneyTransferException.class)
    public void saveAccountTransactionWithNullAccountFromThrowsMoneyTransferException() {
        AccountTransaction accountTransaction = TestDataFactory.createAccountTransaction();
        accountTransaction.setAccountFrom(null);

        transactionRepository.save(accountTransaction);
    }

    @Test(expected = MoneyTransferException.class)
    public void saveAccountTransactionWithNullAccountToThrowsMoneyTransferException() {
        AccountTransaction accountTransaction = TestDataFactory.createAccountTransaction();
        accountTransaction.setAccountTo(null);

        transactionRepository.save(accountTransaction);
    }

    @Test(expected = MoneyTransferException.class)
    public void saveAccountTransactionWithNullAmountThrowsMoneyTransferException() {
        AccountTransaction accountTransaction = TestDataFactory.createAccountTransaction();
        accountTransaction.setAmount(null);

        transactionRepository.save(accountTransaction);
    }

    @Test(expected = MoneyTransferException.class)
    public void saveAccountTransactionWithNullStatusThrowsMoneyTransferException() {
        AccountTransaction accountTransaction = TestDataFactory.createAccountTransaction();
        accountTransaction.setAmount(null);

        transactionRepository.save(accountTransaction);
    }

    @Test
    public void getByIdReturnSavedTransactionSuccess() {
        AccountTransaction accountTransactionSaved =
                transactionRepository.save(TestDataFactory.createAccountTransaction());

        Optional<AccountTransaction> accountTransactionFind =
                transactionRepository.getById(accountTransactionSaved.getTransactionId());

        assertFields(accountTransactionSaved, accountTransactionFind);
    }

    @Test
    public void getByIdReturnEmptyResultSuccess() {
        Optional<AccountTransaction> accountTransactionFind =
                transactionRepository.getById(-1L);

        Assert.assertFalse(accountTransactionFind.isPresent());
    }

    @Test
    public void updateStatusTransactionSuccess() throws SQLException {
        Connection connection = DataSource.getConnection();

        AccountTransaction accountTransactionSaved =
                transactionRepository.save(TestDataFactory.createAccountTransaction());

        transactionRepository.updateStatusTransaction(accountTransactionSaved.getTransactionId(), TransactionStatus.SUCCESS, connection);
        accountTransactionSaved.setStatus(TransactionStatus.SUCCESS);

        Optional<AccountTransaction> accountTransactionFind =
                transactionRepository.getById(accountTransactionSaved.getTransactionId());

        assertFields(accountTransactionSaved, accountTransactionFind);
    }

    private void assertFields(AccountTransaction accountTransactionSaved, Optional<AccountTransaction> accountTransactionFind) {
        Assert.assertEquals(accountTransactionFind.get().getAccountFrom(), accountTransactionSaved.getAccountFrom());
        Assert.assertEquals(accountTransactionFind.get().getAccountTo(), accountTransactionSaved.getAccountTo());
        Assert.assertEquals(accountTransactionFind.get().getAmount(), accountTransactionSaved.getAmount());
        Assert.assertEquals(accountTransactionFind.get().getStatus(), accountTransactionSaved.getStatus());
        Assert.assertEquals(accountTransactionFind.get().getTransactionId(), accountTransactionSaved.getTransactionId());
    }
}