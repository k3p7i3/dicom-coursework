package hse.cs.se.user.service.dicom.data.model

import java.util.*

class DicomList(
    val dicoms: List<DicomId>
) {
    data class DicomId(
        val dicomUID: UUID,
        var s3DicomFilePath: String? = null,
        val instanceNumber: Int? = null,
        var hasImageData: Boolean,
        var numberOfFrames: Int? = null,
    )
}