package hse.cs.se.user.service.dicom

import hse.cs.se.user.service.dicom.data.model.*
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RequestMapping("/api/v1/dicom")
@RestController
class DicomController(
    private val dicomClient: DicomStorageClient,
    private val accessCheckService: AccessCheckService
) {

    @PostMapping("/upload")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #filePath)")
    fun uploadDicom(
        @RequestPart multipartFile: MultipartFile,
        @RequestParam filePath: String
    ): ResponseEntity<String> {
        val dir = filePath.substringBeforeLast("/")
        val fileName = filePath.substringAfterLast("/")

        return dicomClient.uploadDicom(multipartFile, dir, fileName)
    }

    @GetMapping("/get-info")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #filePath)")
    fun getDicomInfo(
        @RequestParam filePath: String
    ): ResponseEntity<DicomFullInfo> {
        return dicomClient.getDicomInfo(filePath)
    }

    @GetMapping("/get-frame")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #filePath)")
    fun getDicomFrame(
        @RequestParam filePath: String,
        @RequestParam frame: Int = 1
    ): ResponseEntity<ByteArray> {
        return dicomClient.getDicomFrame(filePath, frame)
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #filePath)")
    fun deleteDicom(
        @RequestBody filePath: String
    ): ResponseEntity<String> {
        return dicomClient.deleteDicom(filePath)
    }

    @PostMapping("/rename")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #renameRequest.oldFilePath)")
    fun renameDicom(
        @RequestBody renameRequest: RenameFileRequest
    ): ResponseEntity<String> {
        return dicomClient.renameDicom(renameRequest)
    }

    @PostMapping("/dir/create")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #dirPath)")
    fun createDirectory(
        @RequestBody dirPath: String
    ): ResponseEntity<String> {
        return dicomClient.createDirectory(dirPath)
    }

    @DeleteMapping("/dir/delete")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #dirPath)")
    fun deleteDirectory(
        @RequestBody dirPath: String
    ): ResponseEntity<String> {
        return dicomClient.deleteDirectory(dirPath)
    }


    @PostMapping("/dir/rename")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #renameRequest.oldFilePath)")
    fun renameDirectory(@RequestBody renameRequest: RenameFileRequest): ResponseEntity<String> {
        return dicomClient.renameDirectory(renameRequest)
    }

    @GetMapping("/dir/get-content")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #dirPath)")
    fun getDirectoryContents(
        @RequestParam dirPath: String
    ): ResponseEntity<GetDirectoryContentsResult> {
        return dicomClient.getDirectoryContents(dirPath)
    }

    @GetMapping("/patients")
    fun getPatientList(@RequestParam username: String): ResponseEntity<PatientList> {
        return dicomClient.getPatientList(accessCheckService.getDomain(username))
    }

    @GetMapping("/studies")
    fun getStudyList(@RequestParam patientUid: UUID): ResponseEntity<StudyList> {
        return dicomClient.getStudyList(patientUid)
    }

    @GetMapping("/series")
    fun getSeriesList(@RequestParam studyUid: String): ResponseEntity<SeriesList> {
        return dicomClient.getSeriesList(studyUid)
    }

    @GetMapping("/dicom-list")
    fun getDicomList(@RequestParam seriesUid: String): ResponseEntity<DicomList> {
        return dicomClient.getDicomList(seriesUid)
    }

    @GetMapping("/download")
    @PreAuthorize("@accessCheckService.hasAccessToResource(authentication.principal, #filePath)")
    fun downloadDicom(@RequestParam filePath: String): ResponseEntity<ByteArray> {
        return dicomClient.downloadDicom(filePath)
    }

}