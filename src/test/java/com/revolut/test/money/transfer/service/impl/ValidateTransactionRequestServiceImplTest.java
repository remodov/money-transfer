package com.revolut.test.money.transfer.service.impl;

import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.service.ValidateTransactionRequestService;
import com.revolut.test.money.transfer.test.utils.TestDataFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ValidateTransactionRequestServiceImplTest {
    private ValidateTransactionRequestService validateTransactionRequestService =
            new ValidateTransactionRequestServiceImpl();

    @Test
    public void validateGetAmountLessZeroMessageSuccess() {
        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();
        transactionTransferRequest.setAmount(-1D);

        Optional<String> errorMessage =
                validateTransactionRequestService.validate(transactionTransferRequest);

        Assert.assertEquals(errorMessage.get(),"Wrong amount value, must be more then zero");
    }

    @Test
    public void validateAccountFromWrongLengthMessageSuccess() {
        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();
        transactionTransferRequest.setAccountFrom("123");

        Optional<String> errorMessage =
                validateTransactionRequestService.validate(transactionTransferRequest);

        Assert.assertEquals(errorMessage.get(),"Wrong account from value, length must be 20");
    }

    @Test
    public void validateAccountToWrongLengthMessageSuccess() {
        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();
        transactionTransferRequest.setAccountTo("123");

        Optional<String> errorMessage =
                validateTransactionRequestService.validate(transactionTransferRequest);

        Assert.assertEquals(errorMessage.get(),"Wrong account to value, length must be 20");
    }

    @Test
    public void validateAccountToEqualsAccountFromMessageSuccess() {
        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();
        transactionTransferRequest.setAccountTo(transactionTransferRequest.getAccountFrom());

        Optional<String> errorMessage =
                validateTransactionRequestService.validate(transactionTransferRequest);

        Assert.assertEquals(errorMessage.get(),"Account from could not equals account to");
    }

    @Test
    public void validateSuccess() {
        TransactionTransferRequest transactionTransferRequest =
                TestDataFactory.createTransactionTransferRequest();

        Optional<String> errorMessage =
                validateTransactionRequestService.validate(transactionTransferRequest);

        Assert.assertFalse(errorMessage.isPresent());
    }
}