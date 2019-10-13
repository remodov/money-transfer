CREATE TABLE IF NOT EXISTS account_balance (
    account_no VARCHAR(20) PRIMARY KEY,
    balance DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS account_transactions (
    id bigint auto_increment,
    account_no_from VARCHAR(20) NOT NULL,
    account_no_to VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    init_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DOUBLE NOT NULL
);