package com.revolut.test.money.transfer.repository;

import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.entity.AccountTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * CRUD for work with AccountTransaction Entity
 */
public interface TransactionRepository {
    AccountTransaction save(AccountTransaction accountTransaction);

    Optional<AccountTransaction> getById(Long transactionId);

    void updateStatusTransaction(Long transactionId, TransactionStatus transactionStatus, Connection connection) throws SQLException;
}
