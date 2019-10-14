CREATE TABLE if not exists ACCOUNT_BALANCE (
    account_no VARCHAR(20) PRIMARY KEY,
    balance DECIMAL NOT NULL
);

CREATE TABLE if not exists account_transactions (
    id bigint auto_increment,
    account_no_from VARCHAR(20) NOT NULL,
    account_no_to VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    init_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL NOT NULL
);