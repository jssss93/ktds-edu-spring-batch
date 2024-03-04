CREATE DATABASE testdb;
use testdb;

CREATE TABLE user (
    id INT NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
	email VARCHAR(100),
	gender VARCHAR(100),
	ip_address VARCHAR(100),
	country_code VARCHAR(100)
);

CREATE TABLE Customer (
    id INT NOT NULL,
    name VARCHAR(100),
    age VARCHAR(100)
);
