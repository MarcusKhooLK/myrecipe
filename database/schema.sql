drop schema if exists myrecipedb;

create schema myrecipedb;

use myrecipedb;

create table test(
    test_id int not null auto_increment,
    name varchar(16),
    primary key(test_id)
);

create table user(
    email varchar(64) not null,
    username char(20) not null,
    password varchar(286) not null,
    primary key(email)
);