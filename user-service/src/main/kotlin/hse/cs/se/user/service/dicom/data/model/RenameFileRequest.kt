package hse.cs.se.user.service.dicom.data.model

data class RenameFileRequest(
    val oldFilePath: String,
    val newFilePath: String
)