package hse.cs.se.study.storage

import hse.cs.se.study.storage.data.model.DicomInstance
import hse.cs.se.study.storage.data.model.Patient
import hse.cs.se.study.storage.data.model.Series
import hse.cs.se.study.storage.data.model.Study
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam
import org.dcm4che3.io.DicomInputStream
import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import java.lang.RuntimeException
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

import javax.imageio.ImageIO
import javax.imageio.ImageReader

class DicomParser(
    private val dicomFile: File
) : Closeable {

    private val attributes = DicomInputStream(dicomFile).use { it.readDataset() }

    private val dicomTimeFormatter = DateTimeFormatter.ofPattern(DICOM_TIME_FORMAT)

    fun extractImages(): List<BufferedImage> {
        val iterator: Iterator<ImageReader> =
            ImageIO.getImageReadersByFormatName("DICOM")

        if (!iterator.hasNext()) {
            throw RuntimeException("Found no ImageReader for DICOM format")
        }

        val reader = iterator.next()
        val dicomStream = DicomInputStream(dicomFile)
        reader.input = dicomStream

        val framesNum = reader.getNumImages(true)

        val images = (0..<framesNum)
            .map { index ->
                reader.read(index, DicomImageReadParam())
            }

        reader.dispose()
        dicomStream.close()

        return images
    }

    fun parsePatient() = Patient(
        patientId = attributes.getString(Tag.PatientID),
        patientName = formatName(attributes.getString(Tag.PatientName)),
        patientBirthDate = dateToLocalDate(attributes.getDate(Tag.PatientBirthDate)),
        patientSex = stringToPatientSex(attributes.getString(Tag.PatientSex)),
        patientComments = attributes.getString(Tag.PatientComments)
    )

    fun parseStudy() = Study(
        studyUid = attributes.getString(Tag.StudyInstanceUID),
        studyId = attributes.getString(Tag.StudyID),
        studyDate = dateToLocalDate(attributes.getDate(Tag.StudyDate)),
        studyTime = dicomTimeToLocalTime(attributes.getString(Tag.StudyTime)),
        studyDescription = attributes.getString(Tag.StudyDescription),
        patientStudy = parsePatientStudy(attributes),
        referringPhysician = parseReferringPhysician(attributes)
    )

    fun parseSeries() = Series(
        seriesUid = attributes.getString(Tag.SeriesInstanceUID),
        studyUid = attributes.getString(Tag.StudyInstanceUID),
        seriesNumber = stringIntegerToInt(attributes.getString(Tag.SeriesNumber)),
        modality = attributes.getString(Tag.Modality),
        seriesDate = dateToLocalDate(attributes.getDate(Tag.SeriesDate)),
        seriesTime = dicomTimeToLocalTime(attributes.getString(Tag.SeriesTime)),
        seriesDescription = attributes.getString(Tag.SeriesDescription),
        performingPhysicianName = formatName(attributes.getString(Tag.PerformingPhysicianName)),
        operatorName = formatName(attributes.getString(Tag.OperatorsName)),
        bodyPartExamined = attributes.getString(Tag.BodyPartExamined),
        equipment = parseEquipment(attributes)
    )

    fun parseDicomInstance(): DicomInstance {
        val hasImageData = attributes.contains(Tag.PixelData)
        return DicomInstance(
            seriesUid = attributes.getString(Tag.SeriesInstanceUID),
            SOPInstanceUID = attributes.getString(Tag.SOPInstanceUID),
            instanceNumber = stringIntegerToInt(attributes.getString(Tag.InstanceNumber)),
            hasImageData = hasImageData,
            windowCenter = attributes.getString(Tag.WindowCenter),
            windowWidth = attributes.getString(Tag.WindowWidth),
            photometric = attributes.getString(Tag.PhotometricInterpretation),
            anatomicRegion = attributes
                .getSequence(Tag.AnatomicRegionSequence)
                ?.firstOrNull()?.getString(Tag.CodeMeaning),
            numberOfFrames = when (hasImageData) {
                true -> stringIntegerToInt(
                    attributes.getString(Tag.NumberOfFrames)
                ) ?: 1
                false -> null
            }
        )
    }

    override fun close() {
        attributes.clear()
    }

    private fun parsePatientStudy(
        attributes: Attributes
    ) = Study.PatientStudy(
        patientAge = attributes.getString(Tag.PatientAge),
        patientSize = attributes.getString(Tag.PatientSize),
        patientWeight = attributes.getString(Tag.PatientWeight),
        medicalAlerts = attributes.getString(Tag.MedicalAlerts),
        occupation = attributes.getString(Tag.Occupation),
        smokingStatus = stringToSmokingStatus(attributes.getString(Tag.SmokingStatus)),
        additionalPatientHistory = attributes.getString(Tag.AdditionalPatientHistory),
        patientState = attributes.getString(Tag.PatientState),
        patientAddress = attributes.getString(Tag.PatientAddress),
        patientPhoneNumbers = attributes.getString(Tag.PatientTelephoneNumbers),
        patientCountry = attributes.getString(Tag.CountryOfResidence),
        patientRegion = attributes.getString(Tag.RegionOfResidence)
    )
        .takeIf {
            listOf<Any?>(
                it.patientAge, it.patientSize, it.patientWeight,
                it.medicalAlerts, it.occupation, it.smokingStatus,
                it.additionalPatientHistory, it.patientState,
                it.patientAddress, it.patientPhoneNumbers, it.patientCountry, it.patientRegion,
            )
                .any { field -> field != null }
        }

    private fun stringToSmokingStatus(str: String?) =
        when (str) {
            "YES" -> Study.PatientStudy.SmokingStatus.YES
            "NO" -> Study.PatientStudy.SmokingStatus.NO
            else -> null
        }

    private fun parseReferringPhysician(
        attributes: Attributes
    ): Study.Physician? {
        val physicianIdentification = attributes.getSequence(
            Tag.ReferringPhysicianIdentificationSequence
        )?.firstOrNull()

        return Study.Physician(
            physicianName = formatName(attributes.getString(Tag.ReferringPhysicianName)),
            institutionName = physicianIdentification?.getString(Tag.InstitutionName),
            institutionAddress = physicianIdentification?.getString(Tag.InstitutionAddress),
            departmentName = physicianIdentification?.getString(Tag.InstitutionalDepartmentName),
            physicianAddress = physicianIdentification?.getString(Tag.PersonAddress),
            physicianTelephone = physicianIdentification?.getString(Tag.PersonTelephoneNumbers)
        )
            .takeIf {
                listOf<Any?>(
                    it.physicianName, it.institutionName, it.institutionAddress,
                    it.departmentName, it.physicianAddress, it.physicianTelephone
                )
                    .any { field -> field != null }
            }
    }

    private fun parseEquipment(
        attributes: Attributes
    ) = Series.Equipment(
        manufacturer = attributes.getString(Tag.Manufacturer),
        institutionName = attributes.getString(Tag.InstitutionName),
        institutionAddress = attributes.getString(Tag.InstitutionAddress),
        stationName = attributes.getString(Tag.StationName),
        institutionalDepartmentName = attributes.getString(Tag.InstitutionalDepartmentName)
    )
        .takeIf {
            listOf<Any?>(
                it.manufacturer, it.institutionName,
                it.institutionAddress, it.stationName,
                it.institutionalDepartmentName
            )
                .any { field -> field != null }
        }

    private fun stringToPatientSex(str: String?) =
        when (str) {
            "F" -> Patient.Sex.FEMALE
            "M" -> Patient.Sex.MALE
            "O" -> Patient.Sex.OTHER
            else -> null
        }

    private fun dicomTimeToLocalTime(dicomTime: String?) =
        dicomTime?.let {
            LocalTime.from(dicomTimeFormatter.parse(it.take(DICOM_TIME_FORMAT.length)))
        }

    private fun dateToLocalDate(date: Date?) =
        date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()

    private fun stringIntegerToInt(stringInt: String?) =
        stringInt?.trim()?.toIntOrNull()

    private fun formatName(name: String?) =
        name?.replace(Regex("(\\^)+"), " ")

    companion object {
        const val DICOM_TIME_FORMAT = "HHmmss"
    }
}
