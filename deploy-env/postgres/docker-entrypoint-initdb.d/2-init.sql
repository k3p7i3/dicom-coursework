\c postgres;

create user "db_user-liquibase" with password 'db_user-liquibase';
create user "db_user" with password 'db_user';

create database db_user with owner = 'postgres';

\c db_user;

create schema ext;

grant usage on schema ext to "db_user-liquibase";
grant usage on schema ext to "db_user";

alter role "db_user-liquibase" set search_path = db_user,ext,public;
alter role "db_user" set search_path = db_user,ext,public;

create extension "uuid-ossp" schema ext;

create schema "db_user" authorization "db_user-liquibase";

grant usage on schema "db_user" to "db_user";

alter default privileges for user "db_user-liquibase" in schema "db_user" grant select, update, insert, delete on tables to "db_user";
alter default privileges for user "db_user-liquibase" in schema "db_user" grant usage, select, update on sequences to "db_user";
