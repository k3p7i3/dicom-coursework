package hse.cs.se.study.storage

import hse.cs.se.study.storage.data.model.DicomFullInfo
import hse.cs.se.study.storage.data.model.web.*
import hse.cs.se.study.storage.s3.S3Client
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("api/storage")
class Controller(
    private val dicomService: DicomService,
    private val fileManagementService: FileManagementService
) {

    @PostMapping("/dicom/upload")
    fun uploadDicom(
        @RequestPart multipartFile: MultipartFile,
        @RequestParam dir: String,
        @RequestParam fileName: String
    ): ResponseEntity<String> {
        val path = pathToDirectoryPath(dir)
        val domain = path.substringBefore("/")

        val dicomFullInfo = dicomService.uploadDicom(
            multipartFile,
            domain = domain,
            path = path,
            fileName = fileName
        )

        dicomFullInfo.dicomInstance.s3DicomFilePath?.let { filePath ->
            return ResponseEntity.ok(filePath)
        }

        return ResponseEntity.internalServerError().build()
    }

    @GetMapping("/dicom/get-info")
    fun getDicomInfo(@RequestParam filePath: String): ResponseEntity<DicomFullInfo> {
        val dicomInfo = dicomService.getFullDicomInfo(filePath)
        return ResponseEntity.ok(dicomInfo)
    }

    @GetMapping("/dicom/get-frame", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getDicomFrame(
        @RequestParam filePath: String,
        @RequestParam frame: Int = 1
    ): ResponseEntity<ByteArray> {
        val frameResponse = fileManagementService.getDicomFrame(
            filePath, frame
        )
        return when (frameResponse.responseType) {
            S3Client.ResponseType.SUCCESS -> ResponseEntity.ok(frameResponse.response)
            else -> ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/dicom/delete")
    fun deleteDicom(@RequestBody filePath: String): ResponseEntity<String> {
        dicomService.deleteDicom(filePath)
        return ResponseEntity.ok(filePath)
    }

    @PostMapping("/dicom/rename")
    fun renameDicom(
        @RequestBody renameRequest: RenameFileRequest
    ): ResponseEntity<String> {
        dicomService.renameDicom(
            renameRequest.oldFilePath,
            renameRequest.newFilePath
        )
        return ResponseEntity.ok(renameRequest.newFilePath)
    }

    @PostMapping("/directory/create")
    fun createDirectory(@RequestBody dirPath: String): ResponseEntity<String> {
        val res = fileManagementService.createDirectory(dirPath)
        return res?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.internalServerError().build()
    }

    @DeleteMapping("/directory/delete")
    fun deleteDirectory(@RequestBody dirPath: String): ResponseEntity<String> {
        dicomService.deleteDirectory(dirPath)
        return ResponseEntity.ok(dirPath)
    }

    @PostMapping("/directory/update")
    fun renameDirectory(
        @RequestBody renameRequest: RenameFileRequest
    ): ResponseEntity<String> {
        dicomService.renameDirectory(
            renameRequest.oldFilePath,
            renameRequest.newFilePath
        )
        return ResponseEntity.ok(renameRequest.newFilePath)
    }

    @GetMapping("/directory/get-content")
    fun getDirectoryContents(
        @RequestParam dirPath: String
    ): ResponseEntity<GetDirectoryContentsResult> {
        return fileManagementService.getDirectoryContents(dirPath)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/dicom/patients")
    fun getPatientList(@RequestParam domain: String): ResponseEntity<PatientList> {
        return ResponseEntity.ok(dicomService.getAllPatientsForDomain(domain))
    }

    @GetMapping("/dicom/studies")
    fun getStudyList(@RequestParam patientUid: UUID): ResponseEntity<StudyList> {
        return ResponseEntity.ok(dicomService.getAllStudyByPatient(patientUid))
    }

    @GetMapping("/dicom/series")
    fun getSeriesList(@RequestParam studyUid: String): ResponseEntity<SeriesList> {
        return ResponseEntity.ok(dicomService.getAllSeriesByStudy(studyUid))
    }

    @GetMapping("/dicom/files")
    fun getDicomList(@RequestParam seriesUid: String): ResponseEntity<DicomList> {
        return ResponseEntity.ok(dicomService.getAllDicomInstancesBySeries(seriesUid))
    }

    @GetMapping("/dicom/download", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadDicom(@RequestParam filePath: String): ResponseEntity<ByteArray> {
        val file = fileManagementService.getDicomRawFile(filePath)

        return when (file.responseType) {
            S3Client.ResponseType.SUCCESS -> ResponseEntity.ok(file.response)
            else -> ResponseEntity.internalServerError().build()
        }
    }

    private fun pathToDirectoryPath(path: String) =
        when {
            path.endsWith("/") -> path.substringBeforeLast("/")
            else -> path
        }
}