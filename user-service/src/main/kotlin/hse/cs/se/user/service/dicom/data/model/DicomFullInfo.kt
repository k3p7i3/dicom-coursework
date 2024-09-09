package hse.cs.se.user.service.dicom.data.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class DicomFullInfo(
    val patient: Patient,
    val study: Study,
    val series: Series,
    val dicomInstance: DicomInstance
) {
    data class Patient(
        var patientUid: UUID? = null,
        var patientId: String? = null,
        var patientName: String? = null,
        var patientBirthDate: LocalDate? = null,
        var patientSex: Sex? = null,
        var patientComments: String? = null,
        var domain: String? = null
    ) {
        enum class Sex { FEMALE, MALE, OTHER }
    }

    data class Study(
        var studyUid: String,
        var patientUid: UUID? = null,
        var studyId: String? = null,
        var studyDate: LocalDate? = null,
        var studyTime: LocalTime? = null,
        var studyDescription: String? = null,
        var patientStudy: PatientStudy? = null,
        var referringPhysician: Physician? = null
    ) {

        data class PatientStudy(
            var patientAge: String? = null,
            var patientSize: String? = null,
            var patientWeight: String? = null,
            var medicalAlerts: String? = null,
            var occupation: String? = null,
            var smokingStatus: SmokingStatus? = null,
            var additionalPatientHistory: String? = null,
            var patientState: String? = null,
            var patientAddress: String? = null,
            var patientPhoneNumbers: String? = null,
            var patientCountry: String? = null,
            var patientRegion: String? = null
        ) {
            enum class SmokingStatus {
                YES, NO
            }
        }

        data class Physician(
            var physicianName: String? = null,
            var institutionName: String? = null,
            var institutionAddress: String? = null,
            var departmentName: String? = null,
            var physicianAddress: String? = null,
            var physicianTelephone: String? = null
        )
    }

    data class Series(
        var seriesUid: String,
        var studyUid: String,
        var seriesNumber: Int? = null,
        var modality: String? = null,
        var seriesDate: LocalDate? = null,
        var seriesTime: LocalTime? = null,
        var seriesDescription: String? = null,
        var performingPhysicianName: String? = null,
        var operatorName: String? = null,
        var bodyPartExamined: String? = null,
        var equipment: Equipment? = null
    ) {
        data class Equipment(
            var manufacturer: String? = null,
            var institutionName: String? = null,
            var institutionAddress: String? = null,
            var stationName: String? = null,
            var institutionalDepartmentName: String? = null
        )
    }

    data class DicomInstance(
        var dicomUid: UUID? = null,
        var seriesUid: String,
        var SOPInstanceUID: String? = null,
        var instanceNumber: Int? = null,
        var anatomicRegion: String? = null,
        var hasImageData: Boolean,
        var windowCenter: String? = null,
        var windowWidth: String? = null,
        var photometric: String? = null,
        var numberOfFrames: Int? = null,
        var s3DicomFilePath: String? = null,
        var s3ImagePathPrefix: String? = null
    )
}
