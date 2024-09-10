\c postgres;

create user "db_study-liquibase" with password 'db_study-liquibase';
create user "db_study" with password 'db_study';

create database db_study with owner = 'postgres';

\c db_study;

create schema ext;

grant usage on schema ext to "db_study-liquibase";
grant usage on schema ext to "db_study";

alter role "db_study-liquibase" set search_path = db_study,ext,public;
alter role "db_study" set search_path = db_study,ext,public;

create extension "uuid-ossp" schema ext;

create schema db_study authorization "db_study-liquibase";

grant usage on schema db_study to db_study;

alter default privileges for user "db_study-liquibase" in schema db_study grant select, update, insert, delete on tables to db_study;
alter default privileges for user "db_study-liquibase" in schema db_study grant usage, select, update on sequences to db_study;
