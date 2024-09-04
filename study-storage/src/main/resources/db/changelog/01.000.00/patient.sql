--kiquibase formatted sql
--changeset pkopyrina:id-1 logicalFilePath:01.000.00/patient.sql
create table patient
(
    patient_uid         uuid        not null default uuid_generate_v4()
        constraint pk_patient primary key,
    patient_id          varchar     not null
        constraint uq_patient_id unique,
    patient_name        varchar     null,
    patient_birth_date  date        null,
    patient_sex         varchar(10) null,
    patient_comments    text        null,
    domain              varchar     not null
);
--rollback drop table patient;

--changeset pkopyrina:id-2 logicalFilePath:01.000.00/patient.sql
create index ix_patient_domain on patient (domain);
--rollback drop index ix_patient_domain;
