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
    new_version JSON not null,
    log_type varchar(50) not null,
    created DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (customer_id) REFERENCES customer (id)
    ON DELETE CASCADE ON UPDATE CASCADE
);
