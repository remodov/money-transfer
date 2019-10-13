@all
Feature: Client can use REST API for money transfer between accounts

  @success
  Scenario: Success init transaction
    When User init transaction with transfer sum 10
    Then System return 200
     And Transaction status is INIT

  @fail
  Scenario: Incorrect init transaction with negative transfer sum parameter
    When User init transaction with transfer sum -100
    Then System response 400 with error message is Wrong amount value, must be more then zero

  @success
  Scenario: Success status transaction
    When User init transaction with transfer sum 10
     And User check transaction status
    Then System return 200
     And Transaction status is INIT

  @success
  Scenario: Success money transfer between accounts
    When User init transaction with transfer sum 10
     And User confirm transaction and money has been transfer
    Then System return 204
     And User check transaction status
     And Transaction status is SUCCESS

  @fail
  Scenario: Withdrawal - not enough money
    When User init transaction with transfer sum 10000000
     And User confirm transaction and money has been transfer
    Then System response 400 with error message is Not enough money in your account