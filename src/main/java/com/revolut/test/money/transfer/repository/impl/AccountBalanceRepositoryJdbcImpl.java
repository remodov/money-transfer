package com.revolut.test.money.transfer.repository.impl;

import com.revolut.test.money.transfer.config.DataSource;
import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountBalanceRepositoryJdbcImpl implements AccountBalanceRepository {
    private final Logger logger = LogManager.getLogger(AccountBalanceRepositoryJdbcImpl.class);

    @Override
    public Optional<AccountBalance> findAccountBalanceByAccountNo(String accountNo) {
        String sql = "SELECT account_no, balance FROM account_balance where account_no = ?";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, accountNo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AccountBalance accountBalance = new AccountBalance();
                    accountBalance.setAccountNo(rs.getString("account_no"));
                    accountBalance.setAmount(rs.getBigDecimal("balance"));
                    return Optional.of(accountBalance);
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new MoneyTransferException(e.getLocalizedMessage());
        }
    }

    @Override
    public void updateAccountBalance(AccountBalance accountBalanceFrom, AccountBalance accountBalanceTo) {
        String sqlUpdate = "update account_balance set balance = ? where account_no = ?";

        try(Connection connection = DataSource.getConnection();
            PreparedStatement updateBalanceStatement = connection.prepareStatement(sqlUpdate))
        {
            connection.setAutoCommit(false);

            executeBalanceStatement(accountBalanceFrom, updateBalanceStatement);

            executeBalanceStatement(accountBalanceTo, updateBalanceStatement);

            connection.commit();

            connection.setAutoCommit(true);

            logger.debug("Update balance success from {} to {} ", accountBalanceFrom, accountBalanceTo);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new MoneyTransferException(e.getLocalizedMessage());
        }
    }

    private void executeBalanceStatement(AccountBalance accountBalanceFrom,
                                         PreparedStatement updateBalanceStatement) throws SQLException {
        updateBalanceStatement.setBigDecimal(1, accountBalanceFrom.getAmount());
        updateBalanceStatement.setString(2, accountBalanceFrom.getAccountNo());
        updateBalanceStatement.execute();
    }

    @Override
    public List<AccountBalance> findAllAccounts() {
        List<AccountBalance> allAccounts = new ArrayList<>();

        try (Connection connection = DataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT account_no, balance FROM account_balance")) {
                while (rs.next()) {
                    AccountBalance accountBalance = new AccountBalance();
                    accountBalance.setAccountNo(rs.getString("account_no"));
                    accountBalance.setAmount(rs.getBigDecimal("balance"));

                    allAccounts.add(accountBalance);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new MoneyTransferException(e.getLocalizedMessage());
        }
        return allAccounts;
    }
}
