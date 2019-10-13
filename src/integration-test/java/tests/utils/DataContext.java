package tests.utils;

import com.revolut.test.money.transfer.dto.ErrorResponse;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;

public class DataContext {
    public static TransactionTransferResponse transactionTransferResponse;
    public static ErrorResponse errorResponse;
    public static int currentStatusCode = 0;
}
