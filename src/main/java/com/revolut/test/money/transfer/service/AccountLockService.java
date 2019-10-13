package com.revolut.test.money.transfer.service;

/**
 * Service contains all accounts for locking when many threads work
 */
public interface AccountLockService {
    /** Return account lock
     * @param accountNo - account number
     * @return Object - lock
     */
    Object getAccountLock(String accountNo);

    /**
     * Load all accounts from database to memory.
     * Accounts not changed, must invoke one time.
     */
    void initAccountLocks();
}
