package hse.cs.se.study.storage

import hse.cs.se.study.storage.data.model.*
import hse.cs.se.study.storage.data.model.web.DicomList
import hse.cs.se.study.storage.data.model.web.PatientList
import hse.cs.se.study.storage.data.model.web.SeriesList
import hse.cs.se.study.storage.data.model.web.StudyList
import hse.cs.se.study.storage.data.repository.*
import hse.cs.se.study.storage.s3.S3Client
import hse.cs.se.study.storage.utils.NotFoundException
import hse.cs.se.study.storage.utils.logTrace
import hse.cs.se.study.storage.utils.logWarn
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Service
class DicomService(
    private val s3Client: S3Client,
    private val patientRepository: PatientRepository,
    private val studyRepository: StudyRepository,
    private val seriesRepository: SeriesRepository,
    private val dicomInstanceRepository: DicomInstanceRepository,
    private val updateRepository: UpdateEntityRepository,
    private val fileManagementService: FileManagementService
) {
    fun getAllPatientsForDomain(domain: String): PatientList {
        val patients = patientRepository.findAllByDomain(domain)
            .map { patient ->
                PatientList.PatientId(
                    patientUid = patient.patientUid!!,
                    patientId = patient.patientId!!,
                    patientName = patient.patientName
                )
            }

        return PatientList(patients)
    }

    fun getAllStudyByPatient(patientUid: UUID): StudyList {
        val studies = studyRepository.findAllByPatientUid(patientUid)
            .map { study ->
                StudyList.StudyId(
                    studyUid = study.studyUid,
                    studyId = study.studyId,
                    studyDate = study.studyDate,
                    studyTime = study.studyTime
                )
            }
        return StudyList(studies)
    }

    fun getAllSeriesByStudy(studyUid: String): SeriesList {
        val series = seriesRepository.findAllByStudyUid(studyUid)
            .map { series ->
                SeriesList.SeriesId(
                    seriesUid = series.seriesUid,
                    seriesNumber = series.seriesNumber,
                    seriesDate = series.seriesDate,
                    seriesTime = series.seriesTime,
                    modality = series.modality
                )
            }
        return SeriesList(series)
    }

    fun getAllDicomInstancesBySeries(seriesUid: String): DicomList {
        val dicoms = dicomInstanceRepository.findAllBySeriesUid(seriesUid)
            .map { dicom ->
                DicomList.DicomId(
                    dicomUID = dicom.dicomUid!!,
                    s3DicomFilePath = dicom.s3DicomFilePath,
                    instanceNumber = dicom.instanceNumber,
                    hasImageData = dicom.hasImageData,
                    numberOfFrames = dicom.numberOfFrames
                )
            }

        return DicomList(dicoms)
    }

    fun getFullDicomInfo(fullFilePath: String): DicomFullInfo {
        val dicomInstance = dicomInstanceRepository.findByS3DicomFilePath(fullFilePath)
            ?: throw NotFoundException("Not found DicomInstance for file $fullFilePath")

        val series = seriesRepository.findByIdOrNull(dicomInstance.seriesUid)
            ?: throw NotFoundException(
                "Not found Series(uid=${dicomInstance.seriesUid}) for file $fullFilePath"
            )

        val study = studyRepository.findByIdOrNull(series.studyUid)
            ?: throw NotFoundException(
                "Not found Study(uid=${series.studyUid}) for file $fullFilePath"
            )

        val patient = patientRepository.findByIdOrNull(study.patientUid!!)
            ?: throw NotFoundException(
                "Not found patient(uid=${study.patientUid}) for file $fullFilePath"
            )

        return DicomFullInfo(patient, study, series, dicomInstance)
    }

    fun uploadDicom(
        multipartFile: MultipartFile,
        domain: String,
        path: String,
        fileName: String
    ): DicomFullInfo {

        val dicomFile = convertMultipartFileToFile(multipartFile)
        val fullDicomInfo: DicomFullInfo

        DicomParser(dicomFile).use { parser ->

            val rawFileResult =
                fileManagementService.uploadDicomRawFile(dicomFile, path, fileName)
            val framesResult = fileManagementService.uploadImageFrames(
                parser.extractImages(),
                path,
                fileName
            )
            fullDicomInfo = uploadParsedDicomInfo(parser, domain, path, fileName)

            if (fullDicomInfo.dicomInstance.numberOfFrames != framesResult.size) {
                logger.logWarn(
                    "Numbers of frames for $path/$fileName does not match! " +
                        "(differDicom.numberOfFrames=${fullDicomInfo.dicomInstance.numberOfFrames}, " +
                        "extractedImagesNumber=${framesResult.size})"
                )
            }
        }

        if (dicomFile.exists()) {
            dicomFile.delete()
        }

        return fullDicomInfo
    }

    fun deleteDicom(
        filePath: String,
        deleteS3Object: Boolean = true
    ) {
        if (deleteS3Object) {
            val deleted = fileManagementService.deleteFile(filePath)

            if (deleted[S3Client.BucketType.RAW_DICOM]!!.isEmpty()) {
                logger.logWarn(
                    "Could not delete raw dicom file $filePath"
                )
            }

            if (deleted[S3Client.BucketType.DICOM_IMAGE]!!.isEmpty()) {
                logger.logWarn(
                    "Could not delete image frames from file $filePath"
                )
            }
        }

        val dicomInstance = dicomInstanceRepository.findByS3DicomFilePath(filePath)
            ?: throw NotFoundException("Could not found DicomInstance(s3File=$filePath) to delete")

        dicomInstanceRepository.deleteById(dicomInstance.dicomUid!!)

        val seriesUid = dicomInstance.seriesUid
        val shallDeleteSeries = dicomInstanceRepository.existsBySeriesUid(seriesUid).not()

        if (shallDeleteSeries) {
            val studyUid = seriesRepository.getStudyUidBySeriesUid(seriesUid)
                ?: throw NotFoundException("Could not found Series(uid=$seriesUid) to delete")

            seriesRepository.deleteById(seriesUid)

            val shallDeleteStudy = seriesRepository.existsByStudyUid(studyUid).not()
            if (shallDeleteStudy) {

                val patientUid = studyRepository.getPatientUidByStudyUid(studyUid)
                    ?: throw NotFoundException("Could not found Study(uid=$studyUid) to delete")

                studyRepository.deleteById(studyUid)

                val shallDeletePatient = studyRepository.existsByPatientUid(patientUid).not()

                if (shallDeletePatient) {
                    patientRepository.deleteById(patientUid)
                }
            }
        }
    }

    fun renameDicom(
        oldFilePath: String,
        newFilePath: String,
        renameS3Objects: Boolean = true
    ) {
        val dicomInstance = dicomInstanceRepository.findByS3DicomFilePath(oldFilePath)
            ?: throw NotFoundException(
                "Could not found DicomInstance(s3File=$oldFilePath) to rename"
            )

        if (renameS3Objects) {
            val fileRenames = fileManagementService.renameFile(oldFilePath, newFilePath)

            if (fileRenames[S3Client.BucketType.RAW_DICOM]!!.isEmpty()) {
                logger.logWarn("Could not rename raw dicom file " +
                    "from $oldFilePath to $newFilePath")
            }
            if (fileRenames[S3Client.BucketType.DICOM_IMAGE]!!.isEmpty()) {
                logger.logWarn("Could not rename image frames " +
                    "from $oldFilePath to $newFilePath")
            }
        }

        dicomInstanceRepository.save(
            dicomInstance.apply {
                s3DicomFilePath = newFilePath
                s3ImagePathPrefix = removeFileExtension(newFilePath)
            }
        )
    }

    fun deleteDirectory(dirPath: String) {
        val deleted = fileManagementService.deleteDirectory(dirPath)
        deleted[S3Client.BucketType.RAW_DICOM]!!.forEach { deletedFilePath ->
            if (!isDirectory(deletedFilePath)) {
                deleteDicom(deletedFilePath, deleteS3Object = false)
            }
        }
    }

    fun renameDirectory(
        oldDirString: String,
        newDirPath: String
    ) {
        val renamedFiles = fileManagementService.renameFile(
            oldDirString, newDirPath
        )

        val renamedImageDirs = renamedFiles[S3Client.BucketType.DICOM_IMAGE]!!
            .filter { isDirectory(it.oldPath) }

        renamedFiles[S3Client.BucketType.RAW_DICOM]!!
            .forEach { renamedRawFile ->
                if (!isDirectory(renamedRawFile.oldPath)) {

                    val imageDirName = removeFileExtension(renamedRawFile.oldPath) + "/"
                    if (renamedImageDirs.all { it.oldPath != imageDirName }) {
                        logger.logWarn(
                            "Could not rename image frames from $imageDirName to " +
                            removeFileExtension(renamedRawFile.newPath)
                        )
                    }

                    renameDicom(
                        renamedRawFile.oldPath,
                        renamedRawFile.newPath,
                        renameS3Objects = false
                    )
                }
            }
    }

    private fun uploadParsedDicomInfo(
        parser: DicomParser,
        domain: String,
        path: String,
        fileName: String
    ): DicomFullInfo {
        val parsedPatient = parser.parsePatient()
        val parsedStudy = parser.parseStudy()
        val parsedSeries = parser.parseSeries()
        val parsedDicomInstance = parser.parseDicomInstance()

        val patient = savePatient(parsedPatient, domain, studyUid = parsedStudy.studyUid)
        val study = saveStudy(parsedStudy, patientUid = patient.patientUid!!)
        val series = saveSeries(parsedSeries)
        val dicomInstance = saveDicomInstance(parsedDicomInstance, path, fileName)

        return DicomFullInfo(patient, study, series, dicomInstance)
    }

    private fun saveDicomInstance(
        parsedDicomInstance: DicomInstance,
        path: String,
        fileName: String
    ): DicomInstance {
        parsedDicomInstance.s3DicomFilePath = "$path/$fileName"
        parsedDicomInstance.s3ImagePathPrefix = "$path/${removeFileExtension(fileName)}"

        val dicomInstance = dicomInstanceRepository.save(parsedDicomInstance)

        logger.logTrace(
            "Saved DicomInstance(dicomUID=${dicomInstance.dicomUid}, " +
                "seriesUID=${dicomInstance.seriesUid}, " +
                "numOfFrames=${dicomInstance.numberOfFrames})"
        )
        return dicomInstance
    }

    private fun saveSeries(
        parsedSeries: Series
    ): Series {
        val foundSeries: Series? = seriesRepository.findByIdOrNull(parsedSeries.seriesUid)
        foundSeries?.updateWith(parsedSeries)

        foundSeries?.let {
            if (it.studyUid != parsedSeries.studyUid) {
                logger.logWarn("StudyUID=${parsedSeries.studyUid} for " +
                    "seriesUid=${parsedSeries.seriesUid} is different from " +
                    "already saved studyUid=${it.studyUid}")

                it.studyUid = parsedSeries.studyUid
            }
        }

        val series = foundSeries?.let {
            updateRepository.updateSeries(it)
        }
            ?: seriesRepository.save(parsedSeries)

        logger.logTrace("Saved Series(seriesUid=${series.seriesUid}, " +
            "studyUid=${series.studyUid}, seriesNumber=${series.studyUid}")

        return series
    }

    private fun saveStudy(
        parsedStudy: Study,
        patientUid: UUID
    ): Study {
        val foundStudy: Study? = studyRepository.findByIdOrNull(parsedStudy.studyUid)
        foundStudy?.updateWith(parsedStudy)

        val study =
            foundStudy?.let {
                updateRepository.updateStudy(
                    it.apply { this.patientUid = patientUid }
                )
            }
                ?: studyRepository.save(
                    parsedStudy.apply { this.patientUid = patientUid }
                )

        logger.logTrace(
            "Saved Study(studyUid=${study.studyUid}, studyId=${study.studyId})"
        )

        return study
    }

    private fun savePatient(
        parsedPatient: Patient,
        domain: String,
        studyUid: String
    ): Patient {
        parsedPatient.patientId = generatePatientId(parsedPatient)
        parsedPatient.domain = domain

        val patientUid = studyRepository.findByIdOrNull(studyUid)?.patientUid

        val foundPatient = when (patientUid) {
            null -> patientRepository.findByPatientId(parsedPatient.patientId!!)
            else -> patientRepository.findByIdOrNull(patientUid)
        }
        foundPatient?.updateWith(parsedPatient)

        val patient = patientRepository.save(foundPatient ?: parsedPatient)

        logger.logTrace(
            "Saved Patient(uid=${patient.patientUid}, " +
            "id=${patient.patientId})"
        )
        return patient
    }

    private fun generatePatientId(patient: Patient): String =
        if (patient.patientId == null) {
            when (patient.patientName.isNullOrBlank()) {
                true -> "<generated_id>_${UUID.randomUUID()}"
                else -> "<generated_id>_${patient.patientName}"
            }
        } else {
            patient.patientId!!
        }


    private fun removeFileExtension(fileName: String): String =
        fileName.substringBeforeLast(".")

    private fun isDirectory(filePath: String) = filePath.endsWith("/")

    private fun convertMultipartFileToFile(
        multipartFile: MultipartFile
    ): File {
        val file = File(multipartFile.originalFilename ?: multipartFile.name)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(multipartFile.bytes)
        }
        return file
    }

    companion object {

        private val logger = LoggerFactory.getLogger(DicomService::class.java)
    }
}