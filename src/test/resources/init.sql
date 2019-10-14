CREATE TABLE ACCOUNT_BALANCE (
    account_no VARCHAR(20) PRIMARY KEY,
    balance DECIMAL NOT NULL
);

CREATE TABLE account_transactions (
    id bigint auto_increment,
    account_no_from VARCHAR(20) NOT NULL,
    account_no_to VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    init_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL NOT NULL
);

INSERT INTO account_balance(account_no, BALANCE) VALUES
('12345678912345678901', 200),
('12345678912345678902', 20000),
('12345678912345678903', 0)
;

commit;