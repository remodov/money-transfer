package com.revolut.test.money.transfer.test.utils;

import com.revolut.test.money.transfer.config.DataSource;

public abstract class TestDatabase {
    static  {
        DataSource.init();
    }
}
