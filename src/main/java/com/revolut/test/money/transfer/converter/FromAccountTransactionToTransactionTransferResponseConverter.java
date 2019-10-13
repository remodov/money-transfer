package com.revolut.test.money.transfer.converter;

import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountTransaction;

import java.util.function.Function;

public class FromAccountTransactionToTransactionTransferResponseConverter
        implements Function<AccountTransaction, TransactionTransferResponse>
{
    @Override
    public TransactionTransferResponse apply(AccountTransaction accountTransaction) {
        TransactionTransferResponse transactionTransferResponse = new TransactionTransferResponse();

        transactionTransferResponse.setTransactionStatus(accountTransaction.getStatus());
        transactionTransferResponse.setAccountFrom(accountTransaction.getAccountFrom());
        transactionTransferResponse.setAccountTo(accountTransaction.getAccountTo());
        transactionTransferResponse.setAmount(accountTransaction.getAmount());
        transactionTransferResponse.setTransactionId(accountTransaction.getTransactionId());

        return transactionTransferResponse;
    }
}
