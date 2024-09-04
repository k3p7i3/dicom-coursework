--liquibase formatted sql
--changeset pkopyrina:id-1 logicalFilePath:01.000.00/study.sql
create table study
(
    study_uid           varchar     not null
        constraint  pk_study primary key,
    patient_uid         uuid        not null
        references patient(patient_uid);
    study_id            varchar     null,
    study_date          date        null,
    study_time          time        null,
    study_description   text        null,
    patient_study       jsonb       null,
    referring_physician jsonb       null
);
--rollback drop table study;

--changeset pkopyrina:id-2 logicalFilePath:01.000.00/study.sql
create index ix_study_patient_uid on study (patient_uid);
--rollback drop index ix_study_patient_uid;
