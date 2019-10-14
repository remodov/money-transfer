package com.revolut.test.money.transfer.test.utils;

import com.revolut.test.money.transfer.dto.TransactionStatus;
import com.revolut.test.money.transfer.dto.TransactionTransferRequest;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import com.revolut.test.money.transfer.entity.AccountTransaction;
import com.revolut.test.money.transfer.entity.AccountTransactionBuilder;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class TestDataFactory {
    public static TransactionTransferRequest createTransactionTransferRequest() {
        TransactionTransferRequest transactionTransferRequest = new TransactionTransferRequest();
        transactionTransferRequest.setAmount(new BigDecimal("10.11"));
        transactionTransferRequest.setAccountFrom("12345678912345678901");
        transactionTransferRequest.setAccountTo("12345678912345678902");

        return transactionTransferRequest;
    }

    public static TransactionTransferResponse createTransactionTransferResponse() {
        TransactionTransferResponse transactionTransferResponse = new TransactionTransferResponse();
        transactionTransferResponse.setAmount(new BigDecimal("10.11"));
        transactionTransferResponse.setAccountFrom("12345678912345678901");
        transactionTransferResponse.setAccountTo("12345678912345678902");
        transactionTransferResponse.setTransactionId(1L);
        transactionTransferResponse.setTransactionStatus(TransactionStatus.SUCCESS);

        return transactionTransferResponse;
    }

    public static AccountTransaction createAccountTransaction() {
        AccountTransaction accountTransaction =
                new AccountTransactionBuilder()
                        .setAccountFrom("12345678912345678912")
                        .setAccountTo("12345678912345678912")
                        .setAmount(new BigDecimal(200))
                        .setStatus(TransactionStatus.INIT)
                        .createAccountTransaction();

        accountTransaction.setTransactionId(1L);
        return accountTransaction;
    }

    public static class DelegatingServletInputStream extends ServletInputStream {

        private final InputStream sourceStream;

        public DelegatingServletInputStream(InputStream sourceStream) {
            this.sourceStream = sourceStream;
        }

        public final InputStream getSourceStream() {
            return this.sourceStream;
        }

        public int read() throws IOException {
            return this.sourceStream.read();
        }

        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
