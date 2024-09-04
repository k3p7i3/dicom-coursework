--liquibase formatted sql
--changeset pkopyrina:id-1 logicalFilePath:01.000.00/series.sql
create table series
(
    series_uid                  varchar         not null
        constraint pk_series primary key,
    study_uid                   varchar         not null
        references study (study_uid),
    series_number               integer         null,
    modality                    varchar         null,
    series_date                 date            null,
    series_time                 time            null,
    series_description          text            null,
    performing_physician_name   varchar         null,
    operator_name               varchar         null,
    body_part_examined          varchar         null,
    equipment                   jsonb           null
);
--rollback drop table series;

--changeset pkopyrina:id-2 logicalFilePath:01.000.00/series.sql
create index ix_series_study_uid on series (study_uid);
--rollback drop index ix_series_study_uid;
