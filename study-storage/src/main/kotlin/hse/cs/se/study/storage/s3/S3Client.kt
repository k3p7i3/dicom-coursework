package hse.cs.se.study.storage.s3

import hse.cs.se.study.storage.data.model.additional.FileRename
import java.io.File

interface S3Client {

    enum class BucketType {
        RAW_DICOM, DICOM_IMAGE
    }

    enum class ResponseType {
        SUCCESS,
        FAIL,
        DIRECTORY_NOT_FOUND,
        ALREADY_EXIST,
        PARTIAL_DELETE
    }

    data class Response<T>(
        val responseType: ResponseType,
        val response: T? = null
    )

    fun createDirectory(
        bucketType: BucketType,
        dirName: String
    ) : Response<String>

    fun deleteDirectory(
        bucketType: BucketType,
        dirName: String
    ) : Response<List<String>>

    fun deleteFiles(
        bucketType: BucketType,
        fileNames: List<String>
    ) : Response<List<String>>

    fun deleteObject(
        bucketType: BucketType,
        path: String
    ): Response<List<String>>

    fun uploadFile(
        bucketType: BucketType,
        path: String,
        fileName: String,
        file: File
    ) : Response<String>

    fun renameObject(
        bucketType: BucketType,
        oldPath: String,
        newPath: String,
    ) : Response<List<FileRename>>

    fun getFileAsBytes(
        bucketType: BucketType,
        filePath: String
    ) : Response<ByteArray>

    fun getDirectoryContents(
        dirPath: String
    ): Response<List<String>>
}