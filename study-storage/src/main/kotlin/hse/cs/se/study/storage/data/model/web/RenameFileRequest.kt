package hse.cs.se.study.storage.data.model.web
data class RenameFileRequest(
    val oldFilePath: String,
    val newFilePath: String
)