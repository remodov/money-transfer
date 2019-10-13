
package tests.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.test.money.transfer.dto.ErrorResponse;
import com.revolut.test.money.transfer.dto.TransactionTransferResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestTemplate {
    private static String baseUrl = PropertiesConfiguration.getBaseUrl();
    private HttpResponse httpResponse;
    private String body;
    private int responseStatusCode;
    private ObjectMapper objectMapper = new ObjectMapper();

    public RestTemplate executePost(String url, String body) throws IOException {
        HttpPost httpPost = new HttpPost(baseUrl + url);

        System.out.println("POST: " + baseUrl + url + " body: " + body);

        StringEntity entity = new StringEntity(body);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpResponse =  HttpClientBuilder.create().build().execute(httpPost);

        this.body = EntityUtils.toString(httpResponse.getEntity());
        this.responseStatusCode = httpResponse.getStatusLine().getStatusCode();

        System.out.println("POST RESPONSE: " + this.body + " with code: " + this.responseStatusCode);

        initDataContext();

        return this;
    }

    public RestTemplate executeGet(String url) throws IOException {
        HttpGet request = new HttpGet(baseUrl + url);

        System.out.println("GET: " + baseUrl + url);

        request.setHeader("Accept", "application/json");

        httpResponse = HttpClientBuilder.create().build().execute(request);
        HttpEntity entity = httpResponse.getEntity();

        this.body = EntityUtils.toString(entity);

        this.responseStatusCode = httpResponse.getStatusLine().getStatusCode();

        System.out.println("GET RESPONSE: " + this.body + " with code: " + this.responseStatusCode);

        initDataContext();
        return this;
    }

    public RestTemplate executePut(String url) throws IOException {
        HttpPut httpPut = new HttpPut(baseUrl + url);

        System.out.println("PUT: " + baseUrl + url);

        httpResponse = HttpClientBuilder.create().build().execute(httpPut);

        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            this.body = EntityUtils.toString(entity);
        }
        else {
            this.body = null;
        }

        if (httpResponse.getStatusLine() != null) {
            this.responseStatusCode = httpResponse.getStatusLine().getStatusCode();
        }
        else {
            this.responseStatusCode = 0;
        }

        System.out.println("GET RESPONSE: " + this.body + " with code: " + this.responseStatusCode);

        initDataContext();
        return this;
    }

    private void initDataContext() throws JsonProcessingException {
        if (responseStatusCode != 200 && responseStatusCode != 204) {
            if (StringUtils.isNotBlank(body)) {
                DataContext.errorResponse = objectMapper.readValue(body, ErrorResponse.class);
            }
            DataContext.transactionTransferResponse = null;
        }
        else {
            if (StringUtils.isNotBlank(body)) {
                DataContext.transactionTransferResponse =
                        objectMapper.readValue(body, TransactionTransferResponse.class);
            }
            DataContext.errorResponse  = null;
        }
        DataContext.currentStatusCode = responseStatusCode;
    }
}
