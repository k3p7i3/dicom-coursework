--liquibase formatted sql
--changeset pkopyrina:id-1 logicalFilePath:01.000.00/dicom_instance.sql
create table dicom_instance
(
    dicom_uid               uuid        not null default uuid_generate_v4()
        constraint pk_dicom_instance primary key,
    series_uid              varchar     not null
        references series (series_uid),
    sop_instance_uid        varchar     null,
    instance_number         integer     null,
    anatomic_region         varchar     null,
    has_image_data          boolean     not null,
    window_center           varchar     null,
    window_width            varchar     null,
    photometric             varchar     null,
    number_of_frames        integer     null,
    s3_dicom_file_path      varchar     null
        constraint uq_dicom_instance_s3_file_path unique,
    s3_image_path_prefix    varchar     null
);
--rollback drop table dicom_instance;

--changeset pkopyrina:id-2 logicalFilePath:01.000.00/dicom_instance.sql
create index ix_dicom_instance_series_uid on dicom_instance (series_uid);
--rollback drop index ix_dicom_instance_series_uid;
