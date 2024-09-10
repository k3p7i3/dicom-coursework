package hse.cs.se.user.service.dicom

import hse.cs.se.user.service.dicom.data.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Component
class DicomStorageClient(
    private val restTemplate: RestTemplate,
    @Value("\${web.client.study.storage.url}")
    private val url: String
) {

    fun uploadDicom(
        multipartFile: MultipartFile,
        dir: String,
        fileName: String
    ): ResponseEntity<String> {
        val endpointUrl = "$url/dicom/upload"

        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val converter = MappingJackson2HttpMessageConverter()
        converter.supportedMediaTypes = listOf(MediaType.MULTIPART_FORM_DATA)
        restTemplate.messageConverters.add(converter)
        val builder = MultipartBodyBuilder()
        builder.part("multipartFile", multipartFile.resource)
        val requestEntity = HttpEntity(builder.build(), headers)

        return restTemplate.exchange(
            "$endpointUrl?dir=$dir&fileName=$fileName",
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
    }

    fun getDicomInfo(
        filePath: String
    ): ResponseEntity<DicomFullInfo> {
        val endpointUrl = "$url/dicom/get-info"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?filePath=$filePath",
            HttpMethod.GET,
            requestEntity,
            DicomFullInfo::class.java
        )
    }

    fun getDicomFrame(
        filePath: String,
        frame: Int = 1
    ): ResponseEntity<ByteArray> {
        val endpointUrl = "$url/dicom/get-frame"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?filePath=$filePath&frame=$frame",
            HttpMethod.GET,
            requestEntity,
            ByteArray::class.java
        )
    }

    fun deleteDicom(filePath: String): ResponseEntity<String> {
        val endpointUrl = "$url/dicom/delete"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity(filePath, headers)

        return restTemplate.exchange(
            endpointUrl,
            HttpMethod.DELETE,
            requestEntity,
            String::class.java
        )
    }

    fun renameDicom(renameRequest: RenameFileRequest): ResponseEntity<String> {
        val endpointUrl = "$url/dicom/rename"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity(renameRequest, headers)

        return restTemplate.exchange(
            endpointUrl,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
    }

    fun createDirectory(dirPath: String): ResponseEntity<String> {
        val endpointUrl = "$url/directory/create"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity(dirPath, headers)

        return restTemplate.exchange(
            endpointUrl,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
    }

    fun deleteDirectory(dirPath: String): ResponseEntity<String> {
        val endpointUrl = "$url/directory/delete"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity(dirPath, headers)

        return restTemplate.exchange(
            endpointUrl,
            HttpMethod.DELETE,
            requestEntity,
            String::class.java
        )
    }

    fun renameDirectory(renameRequest: RenameFileRequest): ResponseEntity<String> {
        val endpointUrl = "$url/directory/update"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity(renameRequest, headers)

        return restTemplate.exchange(
            endpointUrl,
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
    }

    fun getDirectoryContents(
        dirPath: String
    ): ResponseEntity<GetDirectoryContentsResult> {
        val endpointUrl = "$url/directory/get-content"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?dirPath=$dirPath",
            HttpMethod.GET,
            requestEntity,
            GetDirectoryContentsResult::class.java
        )
    }

    fun getPatientList(domain: String): ResponseEntity<PatientList> {
        val endpointUrl = "$url/dicom/patients"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?domain=$domain",
            HttpMethod.GET,
            requestEntity,
            PatientList::class.java
        )
    }

    fun getStudyList(patientUid: UUID): ResponseEntity<StudyList> {
        val endpointUrl = "$url/dicom/studies"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?patientUid=$patientUid",
            HttpMethod.GET,
            requestEntity,
            StudyList::class.java
        )
    }

    fun getSeriesList(studyUid: String): ResponseEntity<SeriesList> {
        val endpointUrl = "$url/dicom/series"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?studyUid=$studyUid",
            HttpMethod.GET,
            requestEntity,
            SeriesList::class.java
        )
    }

    fun getDicomList(seriesUid: String): ResponseEntity<DicomList> {
        val endpointUrl = "$url/dicom/files"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?seriesUid=$seriesUid",
            HttpMethod.GET,
            requestEntity,
            DicomList::class.java
        )
    }

    fun downloadDicom(filePath: String): ResponseEntity<ByteArray> {
        val endpointUrl = "$url/dicom/download"
        val headers = HttpHeaders()
        val requestEntity = HttpEntity<Any>(headers)

        return restTemplate.exchange(
            "$endpointUrl?filePath=$filePath",
            HttpMethod.GET,
            requestEntity,
            ByteArray::class.java
        )
    }
}
