package tests.step;

import com.revolut.test.money.transfer.dto.TransactionStatus;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import tests.utils.DataContext;
import tests.utils.RestTemplate;

import java.io.IOException;

import static tests.utils.DataContext.transactionTransferResponse;

public class TransferMoneySteps {
    private RestTemplate restTemplate = new RestTemplate();

    @When("^User init transaction with transfer sum (-?\\d+)$")
    public void initTransactionAndReturnId(int transferSum) throws IOException {
        restTemplate.executePost("/transaction", "{ \"accountFrom\": \"12345678912345678901\", \"accountTo\": \"12345678912345678902\", \"amount\": " + transferSum + " }");
    }

    @Then("^Transaction status is (.*?)$")
    public void checkInitStatusWhenTransactionRegistered(String status) {
        Assert.assertSame(transactionTransferResponse.getTransactionStatus(), TransactionStatus.valueOf(status));
    }

    @Then("^System response (\\d+) with error message is (.*?)$")
    public void systemResponseWithErrorMessage(int responseCode, String errorMessage) {
        Assert.assertEquals(responseCode, DataContext.currentStatusCode);
        Assert.assertEquals(errorMessage, DataContext.errorResponse.getErrorMessage());
    }

    @And("^User check transaction status$")
    public void userCheckTransactionStatus() throws IOException {
        restTemplate.executeGet("/transaction/" + DataContext.transactionTransferResponse.getTransactionId());
    }

    @Then("^System return (\\d+) and transaction status$")
    public void systemReturnAndTransactionStatus(int arg0) {
    }

    @And("^User confirm transaction and money has been transfer$")
    public void userConfirmTransactionAndMoneyHasBeenTransfer() throws IOException {
        restTemplate.executePut("/transaction/" + DataContext.transactionTransferResponse.getTransactionId());
    }

    @Then("^System return (\\d+)$")
    public void systemReturn(int arg0) {
        Assert.assertEquals(arg0, DataContext.currentStatusCode);
    }
}
