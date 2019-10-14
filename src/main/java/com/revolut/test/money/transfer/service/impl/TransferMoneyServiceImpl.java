package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.config.DataSource;
import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;

public class TransferMoneyServiceImpl implements TransferMoneyService {
    private final static Logger logger = LoggerFactory.getLogger(TransferMoneyServiceImpl.class);

    private final AccountBalanceRepository accountBalanceRepository;
    private final TransactionRepository transactionRepository;
    private final AccountLockService accountLockService;

    public TransferMoneyServiceImpl(AccountBalanceRepository accountBalanceRepository,
                                    TransactionRepository transactionRepository,
                                    AccountLockService accountLockService) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.transactionRepository = transactionRepository;
        this.accountLockService = accountLockService;
    }

    public void transfer(TransactionTransferResponse transactionTransferRequest) {
        logger.debug("Start transaction transfer: {}", transactionTransferRequest);

        Object lockAccountFrom = accountLockService.getAccountLock(transactionTransferRequest.getAccountFrom());
        Object lockAccountTo = accountLockService.getAccountLock(transactionTransferRequest.getAccountTo());

        if (transactionTransferRequest.getAccountFrom().compareTo(transactionTransferRequest.getAccountTo()) < 1) {
            lockAccountFrom = accountLockService.getAccountLock(transactionTransferRequest.getAccountTo());
            lockAccountTo = accountLockService.getAccountLock(transactionTransferRequest.getAccountFrom());
            logger.debug("Locking reorder {}", transactionTransferRequest);
        }

        try {
            synchronized (lockAccountFrom) {
                synchronized (lockAccountTo) {
                    AccountBalance balanceFrom =
                            accountBalanceRepository.findAccountBalanceByAccountNo(transactionTransferRequest.getAccountFrom())
                                                    .orElseThrow(() -> new MoneyTransferException("Account not found: " + transactionTransferRequest.getAccountFrom()));

                    AccountBalance balanceTo =
                            accountBalanceRepository.findAccountBalanceByAccountNo(transactionTransferRequest.getAccountTo())
                                                    .orElseThrow(() -> new MoneyTransferException("Account not found: " + transactionTransferRequest.getAccountTo()));

                    if ((balanceFrom.getAmount().subtract(transactionTransferRequest.getAmount())).compareTo(BigDecimal.ZERO) < 0) {
                        throw new MoneyTransferException("Not enough money in your account");
                    }

                    balanceFrom.setAmount(balanceFrom.getAmount().subtract(transactionTransferRequest.getAmount()));
                    balanceTo.setAmount(balanceTo.getAmount().add(transactionTransferRequest.getAmount()));

                    try (Connection connection = DataSource.getConnection()){
                        connection.setAutoCommit(false);

                        accountBalanceRepository.update(balanceFrom, connection);
                        accountBalanceRepository.update(balanceTo, connection);
                        transactionRepository.updateStatusTransaction(transactionTransferRequest.getTransactionId(),
                                                                      TransactionStatus.SUCCESS,
                                                                      connection);

                        connection.commit();
                        connection.setAutoCommit(true);
                    }

                    logger.debug("End transaction transfer: {}", transactionTransferRequest);
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            throw new MoneyTransferException(e.getLocalizedMessage());
        }
    }
}
