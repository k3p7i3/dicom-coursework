--liquibase formatted sql
--changeset pkopyrina:id-1 logicalFilePath:01.000.00/user.sql
create table user_table
(
    id                      uuid        not null default uuid_generate_v4()
        constraint pk_user primary key,
    first_name              varchar     not null,
    last_name               varchar     not null,
    email                   varchar     not null
        constraint uq_user_email unique,
    password                varchar     not null
);
--rollback drop table user_table;

