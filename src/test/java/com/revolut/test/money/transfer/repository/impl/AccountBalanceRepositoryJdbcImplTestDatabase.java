package com.revolut.test.money.transfer.repository.impl;

import com.revolut.test.money.transfer.config.DataSource;
import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.test.utils.TestDatabase;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AccountBalanceRepositoryJdbcImplTestDatabase extends TestDatabase {
    private AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepositoryJdbcImpl();

    @Test
    public void findAccountBalanceByAccountNoSuccess() {
        Optional<AccountBalance> accountBalanceByAccountNo =
                accountBalanceRepository.findAccountBalanceByAccountNo("12345678912345678901");

        Assert.assertEquals(accountBalanceByAccountNo.get().getAccountNo(),"12345678912345678901");
        Assert.assertEquals(accountBalanceByAccountNo.get().getAmount(), new BigDecimal(200000));
    }

    @Test
    public void findAccountBalanceByAccountNotFoundAndReturnEmptySuccess() {
        Optional<AccountBalance> accountBalanceByAccountNo =
                accountBalanceRepository.findAccountBalanceByAccountNo("12345678912345678911");

        Assert.assertFalse(accountBalanceByAccountNo.isPresent());
    }

    @Test
    public void updateAccountBalanceSuccess() throws SQLException {
        Connection connection = DataSource.getConnection();

        AccountBalance accountBalanceFrom = new AccountBalance();
        accountBalanceFrom.setAmount(new BigDecimal(400));
        accountBalanceFrom.setAccountNo("12345678912345678901");

        AccountBalance accountBalanceTo = new AccountBalance();
        accountBalanceTo.setAmount(new BigDecimal(500));
        accountBalanceTo.setAccountNo("12345678912345678903");

        accountBalanceRepository.update(accountBalanceFrom, connection);
        accountBalanceRepository.update(accountBalanceTo, connection);

        Optional<AccountBalance> accountBalanceFromUpdated =
                accountBalanceRepository.findAccountBalanceByAccountNo("12345678912345678901");

        Optional<AccountBalance> accountBalanceToUpdated =
                accountBalanceRepository.findAccountBalanceByAccountNo("12345678912345678903");

        Assert.assertEquals(accountBalanceFromUpdated.get().getAmount(), accountBalanceFrom.getAmount());
        Assert.assertEquals(accountBalanceToUpdated.get().getAmount(), accountBalanceTo.getAmount());
    }

    @Test
    public void findAllAccountsSuccessLoaded() {
        List<AccountBalance> allAccounts = accountBalanceRepository.findAllAccounts();
        Assert.assertEquals(allAccounts.size() , 3);
    }
}