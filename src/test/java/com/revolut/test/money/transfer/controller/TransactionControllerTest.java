package com.revolut.test.money.transfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.money.transfer.config.ServiceFactory;
import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.service.TransactionService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import com.revolut.test.money.transfer.service.impl.ValidateTransactionRequestServiceImpl;
import com.revolut.test.money.transfer.test.utils.TestDataFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;

public class TransactionControllerTest {
    private ServiceFactory serviceFactoryMock = Mockito.mock(ServiceFactory.class);
    private TransactionService transactionServiceMock = Mockito.mock(TransactionService.class);
    private TransferMoneyService transferMoneyService = Mockito.mock(TransferMoneyService.class);
    private HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
    private HttpServletResponse servletResponse = Mockito.mock(HttpServletResponse.class);
    private TransactionController transactionController;

    private TransactionTransferRequest transactionTransferRequest = TestDataFactory.createTransactionTransferRequest();
    private TransactionTransferResponse transactionTransferResponse = TestDataFactory.createTransactionTransferResponse();

    @Before
    public void init() throws IOException {
        Mockito.when(serviceFactoryMock.createTransactionService())
                .thenReturn(transactionServiceMock);

        Mockito.when(serviceFactoryMock.createTransferMoneyService())
                .thenReturn(transferMoneyService);

        Mockito.when(serviceFactoryMock.createObjectMapper())
                .thenReturn(new ObjectMapper());

        Mockito.when(serviceFactoryMock.createValidateTransactionRequestService())
                .thenReturn(new ValidateTransactionRequestServiceImpl());

        TransactionTransferResponse transactionTransferResponseInit =
                TestDataFactory.createTransactionTransferResponse();
        transactionTransferResponseInit.setTransactionStatus(TransactionStatus.INIT);

        Mockito.when(transactionServiceMock.findById(1L))
                .thenReturn(Optional.of(TestDataFactory.createTransactionTransferResponse()));

        Mockito.when(transactionServiceMock.findById(2L))
                .thenReturn(Optional.empty());

        Mockito.when(transactionServiceMock.findById(3L))
                .thenReturn(Optional.of(transactionTransferResponseInit));

        Mockito.when(servletRequest.getInputStream())
                .thenReturn(new TestDataFactory.DelegatingServletInputStream(System.in));

        transactionController = new TransactionController(serviceFactoryMock);
    }

    @Test
    public void startTransactionMoneyTransferSuccess() throws IOException {
        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction/3");

        transactionController.doPut(servletRequest, servletResponse);

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    public void startTransactionMoneyWhenTransactionInSuccessStatusThenNotFoundSuccess() throws IOException {
        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction/1");

        transactionController.doPut(servletRequest, servletResponse);

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void startTransactionMoneyTransferAndTransactionIdNotFoundSuccess() throws IOException {
        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction/1");

        transactionController.doPut(servletRequest, servletResponse);

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void startTransactionMoneyTransferAndTransactionIdBadRequestSuccess() throws IOException {
        Mockito.when(transactionServiceMock.findById(1L))
                .thenReturn(Optional.empty());

        Mockito.when(servletRequest.getQueryString())
                .thenReturn("http://localhost:8080/transaction");

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        PrintWriter printWriter = new PrintWriter(byteOutputStream);

        Mockito.when(servletResponse.getWriter())
                .thenReturn(printWriter);

        transactionController.doPut(servletRequest, servletResponse);

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void getTransactionStatusByIdSuccess() throws IOException {
        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction/1");

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        PrintWriter printWriter = new PrintWriter(byteOutputStream);

        Mockito.when(servletResponse.getWriter())
                .thenReturn(printWriter);

        transactionController.doGet(servletRequest, servletResponse);

        String responseBody = byteOutputStream.toString();

        Assert.assertEquals(responseBody, "{\"accountFrom\":\"12345678912345678901\",\"accountTo\":\"12345678912345678902\",\"amount\":10.11,\"transactionStatus\":\"SUCCESS\",\"transactionId\":1}");

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_OK);
    }


    @Test
    public void getTransactionStatusByIdNotFoundSuccess() throws IOException {
        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction/2");

        transactionController.doGet(servletRequest, servletResponse);

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void getTransactionStatusByIdBadRequestSuccess() throws IOException {
        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction/2f");

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        PrintWriter printWriter = new PrintWriter(byteOutputStream);

        Mockito.when(servletResponse.getWriter())
                .thenReturn(printWriter);

        transactionController.doGet(servletRequest, servletResponse);

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void initTransactionSuccess() throws IOException {
        Mockito.when(servletRequest.getInputStream())
                .thenReturn(new TestDataFactory.DelegatingServletInputStream(
                        new ByteArrayInputStream(
                                new ObjectMapper().writeValueAsString(transactionTransferRequest).getBytes())));

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        PrintWriter printWriter = new PrintWriter(byteOutputStream);

        Mockito.when(servletResponse.getWriter())
                .thenReturn(printWriter);

        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction");

        Mockito.when(transactionServiceMock.initTransaction(transactionTransferRequest))
                .thenReturn(transactionTransferResponse);

        transactionController.doPost(servletRequest, servletResponse);

        String responseBody = byteOutputStream.toString();

        Assert.assertEquals(responseBody, "{\"accountFrom\":\"12345678912345678901\",\"accountTo\":\"12345678912345678902\",\"amount\":10.11,\"transactionStatus\":\"SUCCESS\",\"transactionId\":1}");

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void initTransactionNorValidMessageWithWrongAmountSuccess() throws IOException {
        transactionTransferRequest.setAmount(BigDecimal.ZERO);

        Mockito.when(servletRequest.getInputStream())
                .thenReturn(new TestDataFactory.DelegatingServletInputStream(
                        new ByteArrayInputStream(
                                new ObjectMapper().writeValueAsString(transactionTransferRequest).getBytes())));

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        PrintWriter printWriter = new PrintWriter(byteOutputStream);

        Mockito.when(servletResponse.getWriter())
                .thenReturn(printWriter);

        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction");

        Mockito.when(transactionServiceMock.initTransaction(transactionTransferRequest))
                .thenReturn(transactionTransferResponse);

        transactionController.doPost(servletRequest, servletResponse);

        String responseBody = byteOutputStream.toString();

        Assert.assertEquals(responseBody, "{\"errorMessage\":\"Wrong amount value, must be more then zero\"}");

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void initTransactionWithNotValidBodySuccess() throws IOException {
        Mockito.when(servletRequest.getInputStream())
                .thenReturn(new TestDataFactory.DelegatingServletInputStream(
                        new ByteArrayInputStream("asd{}".getBytes())));

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        PrintWriter printWriter = new PrintWriter(byteOutputStream);

        Mockito.when(servletResponse.getWriter())
                .thenReturn(printWriter);

        Mockito.when(servletRequest.getRequestURI())
                .thenReturn("http://localhost:8080/transaction");

        Mockito.when(transactionServiceMock.initTransaction(transactionTransferRequest))
                .thenReturn(transactionTransferResponse);

        transactionController.doPost(servletRequest, servletResponse);

        String responseBody = byteOutputStream.toString();

        Assert.assertTrue(responseBody.contains("errorMessage"));

        Mockito.verify(servletResponse, Mockito.times(1))
                .setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}