package com.revolut.test.money.transfer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.money.transfer.config.ServiceFactory;
import com.revolut.test.money.transfer.config.ServiceFactoryImpl;
import com.revolut.test.money.transfer.dto.ErrorResponse;
import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.service.TransactionService;
import com.revolut.test.money.transfer.service.TransferMoneyService;
import com.revolut.test.money.transfer.service.ValidateTransactionRequestService;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * REST for work with transactions.
 */
public class TransactionController extends HttpServlet {
    private final Logger logger = LogManager.getLogger(TransactionController.class);

    private ServiceFactory serviceFactory;

    private final ValidateTransactionRequestService validateTransactionRequestService;
    private final TransactionService transactionService;
    private final TransferMoneyService transferMoneyService;
    private final ObjectMapper objectMapper;

    public TransactionController() {
        this.serviceFactory = new ServiceFactoryImpl();
        this.validateTransactionRequestService = serviceFactory.createValidateTransactionRequestService();
        this.transactionService = serviceFactory.createTransactionService();
        this.transferMoneyService = serviceFactory.createTransferMoneyService();
        this.objectMapper = serviceFactory.createObjectMapper();
    }

    public TransactionController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.validateTransactionRequestService = serviceFactory.createValidateTransactionRequestService();
        this.transactionService = serviceFactory.createTransactionService();
        this.transferMoneyService = serviceFactory.createTransferMoneyService();
        this.objectMapper = serviceFactory.createObjectMapper();
    }

    /**
     * PUT /transaction/{transaction-id} start transaction perform
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long transactionId = getTransactionIdFromPath(req);
            Optional<TransactionTransferResponse> transactionTransfer = transactionService.findById(transactionId);
            if (transactionTransfer.isPresent()) {
                transferMoneyService.transfer(transactionTransfer.get());
                transactionService.updateStatus(transactionId, TransactionStatus.SUCCESS);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            createBadRequestResponse(resp, e.getLocalizedMessage());
        }
    }

    /**
     * GET /transaction/{transaction-id} return transaction info
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long transactionId = getTransactionIdFromPath(req);
            Optional<TransactionTransferResponse> transactionTransfer = transactionService.findById(transactionId);
            if (transactionTransfer.isPresent()) {
                createOkRequestResponse(resp, new ObjectMapper().writeValueAsString(transactionTransfer.get()));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            createBadRequestResponse(resp, e.getLocalizedMessage());
        }
    }

    /**
     * POST /transaction init transaction and return unique transaction id
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            TransactionTransferRequest transactionTransferRequest = getTransactionTransferRequest(req);

            Optional<String> errorMessage = validateTransactionRequestService.validate(transactionTransferRequest);

            if (!errorMessage.isPresent()) {
                TransactionTransferResponse transaction =
                        transactionService.initTransaction(transactionTransferRequest);

                createOkRequestResponse(resp, new ObjectMapper().writeValueAsString(transaction));

                logger.debug("Create transaction: {}", transaction);
            } else {
                createBadRequestResponse(resp, errorMessage.get());
            }
        } catch (Exception e) {
            createBadRequestResponse(resp, e.getLocalizedMessage());
        }
    }

    private Long getTransactionIdFromPath(HttpServletRequest req) {
        String[] split = req.getRequestURI().split("/");
        return new Long(split[split.length - 1]);
    }

    private TransactionTransferRequest getTransactionTransferRequest(HttpServletRequest req) throws IOException {
        String body = getBody(req);
        checkJSON(body);
        return objectMapper.readValue(body, TransactionTransferRequest.class);
    }

    private void createBadRequestResponse(HttpServletResponse resp, String localizedMessage) throws IOException {
        initResponseParams(resp);
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writeBody(objectMapper.writeValueAsString(new ErrorResponse(localizedMessage)), resp);
    }

    private void createOkRequestResponse(HttpServletResponse resp, String json) throws IOException {
        initResponseParams(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
        writeBody(json, resp);
    }

    private void initResponseParams(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void writeBody(String body, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.write(body);
        out.flush();
    }

    private String getBody(HttpServletRequest request) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer, Charset.defaultCharset());
        return writer.toString();
    }

    private void checkJSON(final String json) throws JsonProcessingException {
        objectMapper.readTree(json);
    }
}