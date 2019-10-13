package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.revolut.test.money.transfer.repository.AccountBalanceRepository;
import com.revolut.test.money.transfer.service.AccountLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AccountLocksServiceSimple implements AccountLockService {
    private final static Logger logger = LoggerFactory.getLogger(AccountLocksServiceSimple.class);

    private final ConcurrentHashMap<String, Object> accountLocks = new ConcurrentHashMap<>();

    private final AccountBalanceRepository accountBalanceRepository;

    public AccountLocksServiceSimple(AccountBalanceRepository accountBalanceRepository){
        this.accountBalanceRepository = accountBalanceRepository;
    }

    public void initAccountLocks() {
        logger.debug("Init account locks.");
        accountBalanceRepository.findAllAccounts()
                                .forEach(accountBalance -> accountLocks.put(accountBalance.getAccountNo(), new Object()));
    }

    public Object getAccountLock(String accountNo) {
        return Optional.ofNullable(accountLocks.get(accountNo))
                       .orElseThrow(MoneyTransferException::new);
    }
}
