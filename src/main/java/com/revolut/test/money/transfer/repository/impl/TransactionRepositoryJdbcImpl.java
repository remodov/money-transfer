package com.revolut.test.money.transfer.repository.impl;

import com.revolut.test.money.transfer.config.DataSource;
import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.entity.AccountTransactionBuilder;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class TransactionRepositoryJdbcImpl implements TransactionRepository {
    private final Logger logger = LogManager.getLogger(TransactionRepositoryJdbcImpl.class);

    @Override
    public AccountTransaction save(AccountTransaction accountTransaction) {
        String sqlInsert =
                "insert into account_transactions (account_no_from, account_no_to, status, amount) values(?, ?, ?, ?)";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS))
        {
            insertStatement.setString(1, accountTransaction.getAccountFrom());
            insertStatement.setString(2, accountTransaction.getAccountTo());
            insertStatement.setString(3, accountTransaction.getStatus().name());
            insertStatement.setBigDecimal(4, accountTransaction.getAmount());

            insertStatement.execute();

            try (ResultSet rs = insertStatement.getGeneratedKeys()) {
                rs.next();
                accountTransaction.setTransactionId(rs.getLong(1));
            }

            logger.debug("Transaction {} success save ", accountTransaction);
            return accountTransaction;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new MoneyTransferException();
        }
    }

    @Override
    public Optional<AccountTransaction> getById(Long transactionId) {
        String sql =
                "SELECT account_no_from, account_no_to, status, amount FROM account_transactions where id = ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setLong(1, transactionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AccountTransactionBuilder()
                            .setStatus(TransactionStatus.valueOf(rs.getString("status")))
                            .setAmount(rs.getBigDecimal("amount"))
                            .setAccountTo(rs.getString("account_no_to"))
                            .setAccountFrom(rs.getString("account_no_from"))
                            .setTransactionId(transactionId)
                            .createAccountTransaction());
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new MoneyTransferException();
        }
    }

    @Override
    public void updateStatusTransaction(Long transactionId, TransactionStatus transactionStatus) {
        String sqlUpdate =
                "update account_transactions set status = ? where id = ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sqlUpdate))
        {
            stmt.setString(1, transactionStatus.name());
            stmt.setLong(2, transactionId);

            stmt.execute();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new MoneyTransferException(e.getLocalizedMessage());
        }
        logger.debug("Transaction {} success update status to: {} ", transactionId, transactionStatus);
    }
}
