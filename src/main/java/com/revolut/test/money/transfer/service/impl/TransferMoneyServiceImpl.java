package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.entity.AccountBalance;
import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class TransferMoneyServiceImpl implements TransferMoneyService {
    private final static Logger logger = LoggerFactory.getLogger(TransferMoneyServiceImpl.class);

    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountLockService accountLockService;

    public TransferMoneyServiceImpl(AccountBalanceRepository accountBalanceRepository,
                                    AccountLockService accountLockService) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.accountLockService = accountLockService;
    }

    public void transfer(TransactionTransferRequest transactionTransferRequest) {
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

                    accountBalanceRepository.updateAccountBalance(balanceFrom, balanceTo);

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
