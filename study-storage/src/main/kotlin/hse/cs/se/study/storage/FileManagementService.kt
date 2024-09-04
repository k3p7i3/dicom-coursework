package hse.cs.se.study.storage

import hse.cs.se.study.storage.data.model.additional.FileRename
import hse.cs.se.study.storage.data.model.web.GetDirectoryContentsResult
import hse.cs.se.study.storage.s3.S3Client
import hse.cs.se.study.storage.utils.logError
import hse.cs.se.study.storage.utils.logTrace
import hse.cs.se.study.storage.utils.logWarn
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Service
class FileManagementService(
    private val s3Client: S3Client
) {
    fun getDirectoryContents(
        dirPath: String
    ): GetDirectoryContentsResult? {
        val directoryContents = s3Client.getDirectoryContents(dirPath)

        if (directoryContents.responseType != S3Client.ResponseType.SUCCESS) {
            return null
        }

        val (dirs, files) = directoryContents.response!!
            .partition { it.endsWith("/") }

        return GetDirectoryContentsResult(dirs, files)
    }

    fun createDirectory(
        dirPath: String
    ): String? {
        val rawBucket = s3Client.createDirectory(S3Client.BucketType.RAW_DICOM, dirPath)
        val imageBucket = s3Client.createDirectory(S3Client.BucketType.DICOM_IMAGE, dirPath)

        if (rawBucket.responseType == S3Client.ResponseType.SUCCESS &&
            imageBucket.responseType == S3Client.ResponseType.SUCCESS) {
            return dirPath
        }
        return null
    }

    fun renameFile(
        oldFilePath: String,
        newFilePath: String
    ): Map<S3Client.BucketType, List<FileRename>> {
        val rawBucketRes = s3Client.renameObject(
            S3Client.BucketType.RAW_DICOM,
            oldFilePath,
            newFilePath
        )

        val imageBucketRes = s3Client.renameObject(
            S3Client.BucketType.DICOM_IMAGE,
            removeFileExtension(oldFilePath),
            removeFileExtension(newFilePath)
        )

        return mapOf(
            S3Client.BucketType.RAW_DICOM to when (rawBucketRes.responseType) {
                S3Client.ResponseType.SUCCESS -> rawBucketRes.response!!
                else -> emptyList()
            },

            S3Client.BucketType.DICOM_IMAGE to when(imageBucketRes.responseType) {
                S3Client.ResponseType.SUCCESS -> imageBucketRes.response!!
                else -> emptyList()
            }
        )
    }

    fun deleteFile(
        fullFilePath: String
    ): Map<S3Client.BucketType, List<String>> {
        val deleted = mutableMapOf<S3Client.BucketType, List<String>>()

        deleted[S3Client.BucketType.RAW_DICOM] = s3Client.deleteFiles(
            S3Client.BucketType.RAW_DICOM,
            listOf(fullFilePath)
        ).response ?: emptyList()

        deleted[S3Client.BucketType.DICOM_IMAGE] = s3Client.deleteDirectory(
            S3Client.BucketType.DICOM_IMAGE,
            removeFileExtension(fullFilePath)
        ).response ?: emptyList()

        return deleted
    }

    fun deleteDirectory(
        dirPath: String
    ): Map<S3Client.BucketType, List<String>> {
        val deleted = mutableMapOf<S3Client.BucketType, List<String>>()

        deleted[S3Client.BucketType.RAW_DICOM] = s3Client.deleteDirectory(
            S3Client.BucketType.RAW_DICOM,
            dirPath
        ).response ?: emptyList()

        deleted[S3Client.BucketType.DICOM_IMAGE] = s3Client.deleteDirectory(
            S3Client.BucketType.DICOM_IMAGE,
            dirPath
        ).response ?: emptyList()

        return deleted
    }


    fun uploadDicomRawFile(
        file: File,
        path: String,
        fileName: String
    ): String? {
        val result = s3Client.uploadFile(S3Client.BucketType.RAW_DICOM, path, fileName, file)
        return when (result.responseType) {
            S3Client.ResponseType.SUCCESS -> result.response
            else -> null
        }
    }

    fun uploadImageFrames(
        images: List<BufferedImage>,
        path: String,
        fileName: String
    ) : List<String?> {
        val dir = s3Client.createDirectory(
            S3Client.BucketType.DICOM_IMAGE,
            dirName = "$path/${removeFileExtension(fileName)}"
        ).response

        if (dir.isNullOrEmpty()) {
            logger.logError(
                "Failed directory creation for image frames for dicom file $fileName."
            )
            return emptyList()
        }

        val imageUrls = images.mapIndexed { index, image ->
            uploadImage(image, dir,index + 1)
        }
        val failedUrls = imageUrls.filter { it.responseType != S3Client.ResponseType.SUCCESS }
            .map { it.response }

        when (failedUrls.isEmpty()) {
            true -> logger.logTrace(
                "Successfully uploaded all ${imageUrls.size} " +
                        "image frames for $fileName."
            )
            else -> logger.logWarn(
                "Uploaded ${imageUrls.size - failedUrls.size} image frames for $fileName. " +
                        "Failed to upload ${failedUrls.size} image frames: ${failedUrls}."
            )
        }

        return imageUrls.map { it.response }
    }

    fun uploadImage(
        image: BufferedImage,
        path: String,
        frameNumber: Int = 1
    ): S3Client.Response<String> {
        val frameFileName = DICOM_FRAME_NAME_PATTERN.format(frameNumber)

        val file = File("$path/$frameFileName")
        val fileWriteRes = ImageIO.write(image, "jpg", file)

        if (!fileWriteRes) {
            logger.logError(
                "No appropriate file writer found for jpg " +
                        "format, unable to upload image"
            )

            return S3Client.Response(
                S3Client.ResponseType.FAIL,
                "$path/$frameFileName"
            )
        } else {

            return s3Client.uploadFile(
                S3Client.BucketType.DICOM_IMAGE,
                path,
                frameFileName,
                file
            )
        }
    }

    fun getDicomRawFile(fullFilePath: String) =
        s3Client.getFileAsBytes(
            S3Client.BucketType.RAW_DICOM,
            fullFilePath
        )

    fun getDicomFrame(
        filePath: String,
        frameNumber: Int = 1
    ) = s3Client.getFileAsBytes(
        S3Client.BucketType.DICOM_IMAGE,
        "${removeFileExtension(filePath)}/${DICOM_FRAME_NAME_PATTERN.format(frameNumber)}"
    )

    private fun removeFileExtension(fileName: String): String =
        fileName.substringBeforeLast(".")

    companion object {

        const val DICOM_FRAME_NAME_PATTERN = "%04d.jpg"

        private val logger = LoggerFactory.getLogger(FileManagementService::class.java)
    }
}