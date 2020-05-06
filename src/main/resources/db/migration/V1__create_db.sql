CREATE TABLE company
(
    `id`      INT          NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(255) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE store
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `address`    VARCHAR(255) NOT NULL,
    `company_id` INT,
    PRIMARY KEY (`id`)
);

ALTER TABLE store
    ADD ( FOREIGN KEY (`company_id`) REFERENCES company(`id`)
);

CREATE TABLE receipt
(
    `id`              INT       NOT NULL AUTO_INCREMENT,
    `total`           DOUBLE    NOT NULL,
    `date_time`       DATETIME NOT NULL,
    `store_id`        INT,
    `payment_id` INT,
    `card_id`  INT,
    PRIMARY KEY (`id`)
);

CREATE TABLE payment
(
    `id`   INT AUTO_INCREMENT NOT NULL,
    `type` VARCHAR(4)         NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
);

INSERT INTO payment(`type`)
VALUES ('cash'),
       ('card');

CREATE TABLE card
(
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `card_type`   VARCHAR(255) NOT NULL,
    `number`      VARCHAR(255) NOT NULL UNIQUE,
    `contactless` VARCHAR(6)   NOT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE receipt
    ADD ( FOREIGN KEY (`store_id`) REFERENCES store(`id`), FOREIGN KEY (`payment_id`) REFERENCES payment(`id`), FOREIGN KEY (`card_id`) REFERENCES card(`id`)
);

CREATE TABLE invoice
(
    `id`              INT       NOT NULL AUTO_INCREMENT,
    `total`           DOUBLE    NOT NULL,
    `date_time`       DATETIME NOT NULL,
    `store_id`        INT,
    `customer_id`     INT,
    `payment_id` INT,
    `card_id`  INT,
    PRIMARY KEY (`id`)
);

CREATE TABLE customer
(
    `id`      INT          NOT NULL AUTO_INCREMENT,
    `name`    VARCHAR(255) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

ALTER TABLE invoice
    ADD ( FOREIGN KEY (`store_id`) REFERENCES store(`id`), FOREIGN KEY (`customer_id`) REFERENCES customer(`id`), FOREIGN KEY (`payment_id`) REFERENCES payment(`id`), FOREIGN KEY (`card_id`) REFERENCES card(`id`)
);