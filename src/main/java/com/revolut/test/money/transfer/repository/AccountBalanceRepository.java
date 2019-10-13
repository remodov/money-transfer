package com.revolut.test.money.transfer.repository;

import com.revolut.test.money.transfer.entity.AccountBalance;

import java.util.List;
import java.util.Optional;

/**
 * CRUD for work with AccountBalance Entity
 */
public interface AccountBalanceRepository {
    Optional<AccountBalance> findAccountBalanceByAccountNo(String accountNo);

    /**
     * Update amount from accountBalance from and update amount accountBalance to
     */
    void updateAccountBalance(AccountBalance accountBalanceFrom, AccountBalance accountBalanceTo);

    List<AccountBalance> findAllAccounts();
}
