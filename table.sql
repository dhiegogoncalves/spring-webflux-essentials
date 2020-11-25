CREATE TABLE anime (
 id SERIAL PRIMARY key not null,
 name VARCHAR not null
);


CREATE TABLE dev_user (
 id SERIAL PRIMARY key not null,
 name VARCHAR not null,
 username VARCHAR  not null UNIQUE,
 password VARCHAR  not null,
 authorities VARCHAR  not null
);