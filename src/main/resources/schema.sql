DROP TABLE IF EXISTS product;
DROP SEQUENCE IF EXISTS seq_product;

CREATE SEQUENCE seq_product START WITH 1 INCREMENT BY 1;
CREATE TABLE product
(
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(6,2)
);