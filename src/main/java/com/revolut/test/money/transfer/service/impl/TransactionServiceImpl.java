package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.entity.AccountTransactionBuilder;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.service.TransactionService;

import java.util.Optional;
import java.util.function.Function;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final Function<AccountTransaction, TransactionTransferResponse> converter;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  Function<AccountTransaction, TransactionTransferResponse> converter) {
        this.transactionRepository = transactionRepository;
        this.converter = converter;
    }

    @Override
    public TransactionTransferResponse initTransaction(TransactionTransferRequest request) {
        AccountTransaction savedTransaction =
                transactionRepository.save(new AccountTransactionBuilder().setAccountFrom(request.getAccountFrom())
                                                                          .setAccountTo(request.getAccountTo())
                                                                          .setAmount(request.getAmount())
                                                                          .setStatus(TransactionStatus.INIT)
                                                                          .createAccountTransaction());
        return converter.apply(savedTransaction);
    }

    @Override
    public Optional<TransactionTransferResponse> findById(Long transactionID) {
        return transactionRepository.getById(transactionID)
                                    .map(converter);
    }
}
