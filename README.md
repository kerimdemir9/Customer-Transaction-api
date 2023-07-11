# Customer-Transaction Bank API

## Installation & Execution
The solution requires up-to-date [Docker](https://www.docker.com/products/docker-desktop/) to execute all integration tests with maven surefire plugin.
### Base [Docker](https://www.docker.com/products/docker-desktop/) Images for Integration Tests
* #### Testcontainers version 0.3.3
```sh
docker pull testcontainers/ryuk:0.3.3
```
* #### MySQL version 8.0
```sh
docker pull mysql:8.0
```
---

### Building the Project
```sh
mvn clean install
```

#### DDL Script
```mysql
DROP
DATABASE IF EXISTS bank;

CREATE
DATABASE bank
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE
bank;

DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS customer_log;


CREATE TABLE customer
(
	id INT NOT NULL AUTO_INCREMENT primary key,
    full_name VARCHAR(50) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
    balance Double NOT NULL,
	Unique key phone_number (phone_number)
);

Create table transaction
(
	id INT NOT NULL AUTO_INCREMENT primary key,
    amount Double NOT NULL,
    created      DATETIME DEFAULT CURRENT_TIMESTAMP,
    customer_id INT NOT NULL,
	FOREIGN KEY (customer_id) REFERENCES customer (id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

Create table customer_log
(
	id INT not null auto_increment primary key,
    customer_id INT NOT NULL,
    old_version JSON,
    new_version JSON,
    log_type varchar(50) not null,
    created DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Starting the API
```sh
java -jar target/customerModel.transactionModel-0.0.1-SNAPSHOT.jar
```
### Open API Documentation
```sh
curl localhost:8888/api-docs
```
```shell
http://localhost:8888/api-docs-ui
```



