package com.revolut.test.money.transfer;

import com.revolut.test.money.transfer.config.DataSource;
import com.revolut.test.money.transfer.config.PropertiesConfiguration;
import com.revolut.test.money.transfer.controller.TransactionController;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting application");

        DataSource.init();

        initTestBalance();

        Server server = startServletContainer();

        logger.info("Starting application done");

        server.join();
    }

    private static Server startServletContainer() throws Exception {
        logger.info("Starting servlets");

        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PropertiesConfiguration.getServerPort());

        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
        servletHandler.addServletWithMapping(TransactionController.class, "/transaction/*");

        try {
            server.start();
        } catch (Exception ex) {
            server.stop();
            throw ex;
        }

        logger.info("Starting servlets container done");
        return server;
    }

    private static void initTestBalance() throws SQLException {
        try(Connection connection = DataSource.getConnection();
            Statement st = connection.createStatement()
        ) {
            st.execute(
            "INSERT INTO account_balance(account_no, BALANCE) VALUES\n" +
                    "('12345678912345678901', 200000),\n" +
                    "('12345678912345678902', 200000),\n" +
                    "('12345678912345678903', 0)");
        }
    }
}
