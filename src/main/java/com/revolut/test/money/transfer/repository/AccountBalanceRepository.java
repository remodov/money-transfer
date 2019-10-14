package com.revolut.test.money.transfer.repository;

import com.revolut.test.money.transfer.entity.AccountBalance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * CRUD for work with AccountBalance Entity
 */
public interface AccountBalanceRepository {
    Optional<AccountBalance> findAccountBalanceByAccountNo(String accountNo);

    void update(AccountBalance accountBalance, Connection connection) throws SQLException;

    List<AccountBalance> findAllAccounts();
}
