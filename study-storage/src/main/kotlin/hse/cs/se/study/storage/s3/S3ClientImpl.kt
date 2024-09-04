package hse.cs.se.study.storage.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import hse.cs.se.study.storage.configuration.properties.S3Properties
import hse.cs.se.study.storage.data.model.additional.FileRename
import hse.cs.se.study.storage.utils.logError
import hse.cs.se.study.storage.utils.logTrace
import hse.cs.se.study.storage.utils.logWarn
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

@Service
class S3ClientImpl(
    private val s3Client: AmazonS3,
    private val s3Properties: S3Properties
): S3Client {

    override fun createDirectory(
        bucketType: S3Client.BucketType,
        dirName: String
    ) : S3Client.Response<String> {

        val bucket = getBucketByBucketType(bucketType)
        val metadata = ObjectMetadata()
        metadata.contentLength = 0
        val emptyContent: InputStream = ByteArrayInputStream(byteArrayOf())

        val putObjectRequest = PutObjectRequest(
            bucket,
            "$dirName/",
            emptyContent,
            metadata
        )

        try {
            s3Client.putObject(putObjectRequest)
            logger.logTrace("Created new directory $dirName in bucket $bucket")

            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                dirName
            )
        } catch (exc: Exception) {
            logger.logError(
                "Failed to create new directory $dirName in bucket $bucket",
                 exc
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                dirName
            )
        }
    }

    override fun deleteObject(
        bucketType: S3Client.BucketType,
        path: String
    ): S3Client.Response<List<String>> {
        val bucket = getBucketByBucketType(bucketType)

        return when (isDirectory(bucket, path)) {
            true -> deleteDirectory(bucketType, path)
            false -> deleteFiles(bucketType, listOf(path))
        }
    }

    override fun deleteDirectory(
        bucketType: S3Client.BucketType,
        dirName: String
    ) : S3Client.Response<List<String>> {
        val bucket = getBucketByBucketType(bucketType)

        try {
            if (!s3Client.doesObjectExist(bucket, "$dirName/")) {
                logger.logWarn(
                    "Directory $dirName/ does not exist in bucket $bucket, " +
                        "delete will be skipped "
                )
                return S3Client.Response(
                    S3Client.ResponseType.DIRECTORY_NOT_FOUND,
                    emptyList()
                )
            }

            val nestedObjects = s3Client.listObjectsV2(bucket, "$dirName/")
                .objectSummaries
                .map { it.key }

            val deleteNested = deleteFiles(bucketType, fileNames = nestedObjects)

            if (deleteNested.responseType != S3Client.ResponseType.SUCCESS) {
                logger.logError(
                    "Couldn't delete all objects for directory $dirName/, " +
                        "deleting returned ${deleteNested.responseType}. " +
                        "Directory delete will be skipped"
                )

                return S3Client.Response(
                    S3Client.ResponseType.FAIL,
                    deleteNested.response
                )
            }

            logger.logTrace("Successfully deleted directory $dirName in bucket $bucket")
            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                deleteNested.response
            )
        } catch (exc: Exception) {
            logger.logError("Unexpected error during directory delete!", exc)
            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                emptyList()
            )
        }
    }

    override fun deleteFiles(
        bucketType: S3Client.BucketType,
        fileNames: List<String>
    ) : S3Client.Response<List<String>> {
        val bucket = getBucketByBucketType(bucketType)
        val keys = fileNames.map(DeleteObjectsRequest::KeyVersion)
        val deleteObjectsRequest = DeleteObjectsRequest(bucket)
            .withKeys(keys)
            .withQuiet(false)

        try {
            val deleteResult = s3Client.deleteObjects(deleteObjectsRequest)
            val deletedFiles = deleteResult.deletedObjects
                .map(DeleteObjectsResult.DeletedObject::getKey)
            val deletedQuantity = deleteResult.deletedObjects.size

            val responseType = when (deletedQuantity == fileNames.size) {
                true -> {
                    logger.logTrace(
                        "Successfully deleted $deletedQuantity files"
                    )

                    S3Client.ResponseType.SUCCESS
                }
                else -> {
                    logger.logWarn(
                        "Deleted $deletedQuantity files of ${fileNames.size}, " +
                            "couldn't delete files: ${fileNames.filter { it !in deletedFiles }}"
                    )

                    S3Client.ResponseType.PARTIAL_DELETE
                }
            }

            return S3Client.Response(
                responseType,
                deletedFiles
            )
        } catch (exc: Exception) {
            logger.logError("Unexpected error during file delete!", exc)
            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                emptyList()
            )
        }
    }

    override fun uploadFile(
        bucketType: S3Client.BucketType,
        path: String,
        fileName: String,
        file: File
    ) : S3Client.Response<String> {
        val bucket = getBucketByBucketType(bucketType)
        val filePath = "$path/$fileName"

        try {
            if (!s3Client.doesObjectExist(bucket, "$path/")) {
                logger.logWarn(
                    "Tried uploading file $fileName to not " +
                        "existing directory $path in bucket $bucket"
                )

                return S3Client.Response(
                    S3Client.ResponseType.DIRECTORY_NOT_FOUND,
                    filePath
                )
            }

            if (s3Client.doesObjectExist(bucket, filePath)) {
                logger.logWarn(
                    "File $filePath already exists in bucket $bucket, " +
                        "file uploading will be skipped"
                )

                return S3Client.Response(
                    S3Client.ResponseType.ALREADY_EXIST,
                    filePath
                )
            }

            val putObjectRequest = PutObjectRequest(bucket, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead)

            s3Client.putObject(putObjectRequest)

            logger.logTrace(
                "Successfully uploaded file ($filePath) to bucket $bucket"
            )

            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                filePath
            )
        } catch (exc: Exception) {
            logger.logError(
                "Couldn't upload file $filePath to " +
                    "bucket $bucket due to unexpected error!",
                exc
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                filePath
            )
        }
    }

    override fun renameObject(
        bucketType: S3Client.BucketType,
        oldPath: String,
        newPath: String,
    ) : S3Client.Response<List<FileRename>> {
        val bucket = getBucketByBucketType(bucketType)

        return when (isDirectory(bucket, oldPath)) {
            true -> renameDirectory(bucket, oldPath, newPath)
            false -> renameFile(bucket, oldPath, newPath)
        }
    }


    private fun renameDirectory(
        bucket: String,
        oldPath: String,
        newPath: String
    ) : S3Client.Response<List<FileRename>> {
        try {
            val results = mutableListOf<FileRename>()
            s3Client.listObjectsV2(bucket, "$oldPath/")
                .objectSummaries
                .map(S3ObjectSummary::getKey)
                .forEach { path ->
                    val copyObjectRequest = CopyObjectRequest(
                        bucket,
                        path,
                        bucket,
                        path.replace("$oldPath/", "$newPath/")
                    )
                    s3Client.copyObject(copyObjectRequest)
                    s3Client.deleteObject(bucket, path)

                    results.add(
                        FileRename(
                            oldPath = copyObjectRequest.sourceKey,
                            newPath = copyObjectRequest.destinationKey
                        )
                    )
                }

            logger.logTrace(
                "Successfully renamed directory $oldPath/ to $newPath/ in bucket $bucket"
            )
            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                results
            )
        } catch (exc: Exception) {
            logger.logError(
                "Couldn't rename directory from $oldPath/ to " +
                        "$newPath/ due to unexpected error!",
                exc
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                null
            )
        }
    }

    private fun renameFile(
        bucket: String,
        oldPath: String,
        newPath: String
    ) : S3Client.Response<List<FileRename>> {
        try {
            s3Client.copyObject(
                CopyObjectRequest(bucket, oldPath, bucket, newPath)
            )
            s3Client.deleteObject(
                DeleteObjectRequest(bucket, oldPath)
            )

            logger.logTrace("Successfully renamed file from $oldPath to $newPath")

            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                listOf(FileRename(oldPath, newPath))
            )
        } catch (exc: Exception) {
            logger.logError(
                "Couldn't rename file from $oldPath to " +
                    "$newPath due to unexpected error!",
                exc
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                null
            )
        }
    }

    override fun getFileAsBytes(
        bucketType: S3Client.BucketType,
        filePath: String
    ) : S3Client.Response<ByteArray> {
        val bucket = getBucketByBucketType(bucketType)

        try {
            val byteContent: ByteArray
            s3Client.getObject(bucket, filePath).use {
                byteContent = it.objectContent.readBytes()
            }

            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                byteContent
            )
        } catch (exc: Exception) {
            logger.logError(
                "Couldn't get byte content of file " +
                    "$filePath from bucket $bucket due to unexpected exception!",
                exc
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                byteArrayOf()
            )
        }
    }

    override fun getDirectoryContents(
        dirPath: String
    ): S3Client.Response<List<String>> {
        val bucket = getBucketByBucketType(S3Client.BucketType.RAW_DICOM)
        try {
            val request = ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix("$dirPath/")
                .withDelimiter("/")

            val dirContents = s3Client.listObjectsV2(request).commonPrefixes

            return S3Client.Response(
                S3Client.ResponseType.SUCCESS,
                dirContents
            )
        } catch (exc: Exception) {
            logger.logError(
                "Couldn't get directory $dirPath contents from bucket $bucket.",
                exc
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                emptyList()
            )
        }
    }

    private fun isDirectory(
        bucket: String,
        objectName: String
    ): Boolean = s3Client.doesObjectExist(bucket, "$objectName/")

    private fun getBucketByBucketType(type: S3Client.BucketType) =
        when (type) {
            S3Client.BucketType.RAW_DICOM -> s3Properties.buckets.dicomBucket
            S3Client.BucketType.DICOM_IMAGE -> s3Properties.buckets.imageBucket
        }

    companion object {

        private val logger = LoggerFactory.getLogger(S3ClientImpl::class.java)
    }
}