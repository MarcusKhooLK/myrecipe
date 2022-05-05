drop schema if exists myrecipedb;

create schema myrecipedb;

use myrecipedb;

create table test(
    test_id int not null auto_increment,
    name varchar(16),
    primary key(test_id)
);

create table user(
    user_id int not null auto_increment,
    username varchar(20) not null,
    password varchar(286) not null,
    primary key(user_id)
);

create table recipe(
    recipe_id int not null auto_increment,
    name varchar(64) not null,
    category varchar(32),
    country varchar(32),
    instructions mediumtext not null,
    thumbnail varchar(128),
    youtubeLink varchar(128),
    ingredient0 varchar(32) not null,
    ingredient1 varchar(32),
    ingredient2 varchar(32),
    ingredient3 varchar(32),
    ingredient4 varchar(32),
    ingredient5 varchar(32),
    ingredient6 varchar(32),
    ingredient7 varchar(32),
    ingredient8 varchar(32),
    ingredient9 varchar(32),
    measurement0 varchar(16) not null,
    measurement1 varchar(16),
    measurement2 varchar(16),
    measurement3 varchar(16),
    measurement4 varchar(16),
    measurement5 varchar(16),
    measurement6 varchar(16),
    measurement7 varchar(16),
    measurement8 varchar(16),
    measurement9 varchar(16),
    user_id int not null,
    primary key(recipe_id),
    constraint fk_user_id
        foreign key(user_id) 
        references user(user_id)
);