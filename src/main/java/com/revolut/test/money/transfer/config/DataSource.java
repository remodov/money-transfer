package com.revolut.test.money.transfer.config;

import com.revolut.test.money.transfer.exception.MoneyTransferException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static final Logger logger = LogManager.getLogger(DataSource.class);

    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    public static void init() {
        config.setJdbcUrl(PropertiesConfiguration.getJdbcUrl());
        config.setUsername(PropertiesConfiguration.getJdbcUser());
        dataSource = new HikariDataSource(config);
    }

    private DataSource() {}

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        throw new MoneyTransferException();
    }
}
