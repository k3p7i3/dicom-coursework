package hse.cs.se.study.storage.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("dicom_instance")
data class DicomInstance(
    @Id
    @Column("dicom_uid")
    var dicomUid: UUID? = null,

    @Column("series_uid")
    var seriesUid: String,

    @Column("sop_instance_uid")
    var SOPInstanceUID: String? = null,

    @Column("instance_number")
    var instanceNumber: Int? = null,

    @Column("anatomic_region")
    var anatomicRegion: String? = null,

    @Column("has_image_data")
    var hasImageData: Boolean,

    @Column("window_center")
    var windowCenter: String? = null,

    @Column("window_width")
    var windowWidth: String? = null,

    @Column("photometric")
    var photometric: String? = null,

    @Column("number_of_frames")
    var numberOfFrames: Int? = null,

    @Column("s3_dicom_file_path")
    var s3DicomFilePath: String? = null,

    @Column("s3_image_path_prefix")
    var s3ImagePathPrefix: String? = null
): Persistable<UUID> {

    override fun getId() = dicomUid

    override fun isNew() = (dicomUid == null)
}