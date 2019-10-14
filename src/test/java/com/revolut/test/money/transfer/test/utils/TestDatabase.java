package com.revolut.test.money.transfer.test.utils;

import com.revolut.test.money.transfer.config.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class TestDatabase {
    static  {
        DataSource.init();
        try(Connection connection = DataSource.getConnection();
            Statement st = connection.createStatement()
        ) {
            st.execute(
                    "INSERT INTO account_balance(account_no, BALANCE) VALUES\n" +
                            "('12345678912345678901', 200000),\n" +
                            "('12345678912345678902', 200000),\n" +
                            "('12345678912345678903', 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
