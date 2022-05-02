drop schema if exists myrecipedb;

create schema myrecipedb;

use myrecipedb;

create table test(
    test_id int not null auto_increment,
    name varchar(16),
    primary key(test_id)
);