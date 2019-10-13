package com.revolut.test.money.transfer.config;

import com.revolut.test.money.transfer.exception.MoneyTransferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfiguration {
    private final static Logger logger = LogManager.getLogger(PropertiesConfiguration.class);

    private static int serverPort;
    private static String jdbcUrl;
    private static String jdbcUser;

    static {
        logger.info("Start load application properties");
        try (InputStream input =
                     PropertiesConfiguration.class.getClassLoader()
                                                  .getResourceAsStream("application.properties"))
        {
            Properties prop = new Properties();
            prop.load(input);

            serverPort = new Integer(prop.getProperty("server.port"));
            jdbcUrl = prop.getProperty("h2.url");
            jdbcUser = prop.getProperty("h2.user");

        } catch (IOException io) {
            throw new MoneyTransferException();
        }

        logger.info("Application properties was loaded");
    }

    public static String getJdbcUser() {
        return jdbcUser;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static int getServerPort() {
        return serverPort;
    }
}
