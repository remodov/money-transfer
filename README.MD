# Money transfer service

Service has 3 endpoints:
* Init transaction before send: 
```
POST /transaction
{
  "accountFrom": "12345678912345678901",
  "accountTo": "12345678912345678902",
  "amount": 10.11
}

Response: 200 OK
{
 "accountFrom":"12345678912345678901",
 "accountTo":"12345678912345678902",
 "amount":10.11,
 "transactionStatus":"INIT",
 "transactionId":1
}

Response: 400 BAD REQUEST
{
    "errorMessage": "error"
}
```
* Start transaction:
```
PUT /transaction/{transaction-id}

Response: 204 OK NO CONTENT

Response: 404 NOT FOUND

Response: 400 BAD REQUEST
{
    "errorMessage": "error"
}
```
* Get transaction info by id:
```
GET /transaction/{transaction-id}

Response: 200 OK
{
 "accountFrom":"12345678912345678901",
 "accountTo":"12345678912345678902",
 "amount":10.11,
 "transactionStatus":"INIT",
 "transactionId":1
}

Response: 404 NOT FOUND

Response: 400 BAD REQUEST
{
    "errorMessage": "error"
}
```

### Prerequisites

```
java 8 or height
```

### Build
Create fatJar : /build/libs/money-transfer-1.0-SNAPSHOT-all.jar
```
./gradlew build
```
## Running the unit tests
```
./gradlew test
```

## Running the integration tests
Before running integration tests need to start application:
```
./gradlew build runApplication
```
After application start: 
```
./gradlew integrationTest
```