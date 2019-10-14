package com.revolut.test.money.transfer.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountBalance {
    private String accountNo;
    private BigDecimal amount;

    public AccountBalance(String accountNo, BigDecimal amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public AccountBalance() {
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AccountBalance{" +
                "accountNo='" + accountNo + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountBalance balance = (AccountBalance) o;
        return Objects.equals(accountNo, balance.accountNo) &&
                Objects.equals(amount, balance.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNo, amount);
    }
}
