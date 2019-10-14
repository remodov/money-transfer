package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.converter.FromAccountTransactionToTransactionTransferResponseConverter;
import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.repository.TransactionRepository;
import com.revolut.test.money.transfer.service.TransactionService;
import com.revolut.test.money.transfer.test.utils.TestDataFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Matchers.any;

public class TransactionServiceImplTest {
    private FromAccountTransactionToTransactionTransferResponseConverter converter =
            new FromAccountTransactionToTransactionTransferResponseConverter();

    private TransactionRepository transactionRepositoryMock = Mockito.mock(TransactionRepository.class);

    private TransactionService transactionService =
            new TransactionServiceImpl(transactionRepositoryMock, converter);

    private AccountTransaction accountTransaction = TestDataFactory.createAccountTransaction();
    private TransactionTransferRequest transactionTransferRequest = TestDataFactory.createTransactionTransferRequest();

    @Test
    public void initTransactionSuccessAndAllFieldsSuccessConverted() {
        Mockito.when(transactionRepositoryMock.save(any())).thenReturn(accountTransaction);

        TransactionTransferResponse transactionTransferResponse =
                transactionService.initTransaction(transactionTransferRequest);

        assertFields(transactionTransferResponse);
    }

    @Test
    public void findByIdSuccess() {
        Mockito.when(transactionRepositoryMock.getById(1L))
               .thenReturn(Optional.of(accountTransaction));

        TransactionTransferResponse transactionTransferResponse =
                transactionService.findById(1L).get();

        assertFields(transactionTransferResponse);
    }

    @Test
    public void findByIdNotFoundSuccess() {
        Mockito.when(transactionRepositoryMock.getById(2L))
                .thenReturn(Optional.empty());

        Optional<TransactionTransferResponse> transactionTransferResponse =
                transactionService.findById(2L);

        Assert.assertFalse(transactionTransferResponse.isPresent());
    }

    private void assertFields(TransactionTransferResponse transactionTransferResponse) {
        Assert.assertEquals(transactionTransferResponse.getTransactionStatus(), accountTransaction.getStatus());
        Assert.assertEquals(transactionTransferResponse.getAccountFrom(), accountTransaction.getAccountFrom());
        Assert.assertEquals(transactionTransferResponse.getAccountTo(), accountTransaction.getAccountTo());
        Assert.assertEquals(transactionTransferResponse.getAmount(), accountTransaction.getAmount());
        Assert.assertEquals(transactionTransferResponse.getTransactionId(), accountTransaction.getTransactionId());
    }
}