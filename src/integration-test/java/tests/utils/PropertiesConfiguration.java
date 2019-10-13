package tests.utils;

import com.revolut.test.money.transfer.exception.MoneyTransferException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfiguration {
    private static String baseUrl;

    static {
        try (InputStream input =
                     PropertiesConfiguration.class.getClassLoader()
                                                  .getResourceAsStream("application.properties"))
        {
            Properties prop = new Properties();
            prop.load(input);

            baseUrl = prop.getProperty("baseUrl");
            boolean isDevMode = new Boolean(prop.getProperty("isDevMode"));
        } catch (IOException io) {
            throw new MoneyTransferException();
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
