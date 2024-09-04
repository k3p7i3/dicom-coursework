package hse.cs.se.study.storage.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Table("study")
data class Study(
    @Id
    @Column("study_uid")
    var studyUid: String,

    @Column("patient_uid")
    var patientUid: UUID? = null,

    @Column("study_id")
    var studyId: String? = null,

    @Column("study_date")
    var studyDate: LocalDate? = null,

    @Column("study_time")
    var studyTime: LocalTime? = null,

    @Column("study_description")
    var studyDescription: String? = null,

    @Column("patient_study")
    var patientStudy: PatientStudy? = null,

    @Column("referring_physician")
    var referringPhysician: Physician? = null
): Persistable<String> {

    override fun getId() = studyUid

    override fun isNew() = true

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

    fun updateWith(study: Study) {
        study.studyId?.let { studyId = it }
        study.studyDate?.let { studyDate = it }
        study.studyTime?.let { studyTime = it }
        study.studyDescription?.let { studyDescription = it }

        study.patientStudy?.let { ps ->

            if (patientStudy == null) patientStudy = PatientStudy()

            ps.patientAge?.let { patientStudy!!.patientAge = it }
            ps.patientSize?.let { patientStudy!!.patientSize = it }
            ps.patientWeight?.let { patientStudy!!.patientWeight = it }
            ps.medicalAlerts?.let { patientStudy!!.medicalAlerts = it }
            ps.occupation?.let { patientStudy!!.occupation = it }
            ps.smokingStatus?.let { patientStudy!!.smokingStatus = it }
            ps.additionalPatientHistory?.let { patientStudy!!.additionalPatientHistory = it }
            ps.patientState?.let { patientStudy!!.patientState = it }
            ps.patientAddress?.let { patientStudy!!.patientAddress = it }
            ps.patientPhoneNumbers?.let { patientStudy!!.patientPhoneNumbers = it }
            ps.patientCountry?.let { patientStudy!!.patientCountry = it }
            ps.patientRegion?.let { patientStudy!!.patientRegion= it }
        }

        study.referringPhysician?.let { rp ->
            if (referringPhysician == null) referringPhysician = Physician()

            rp.physicianName?.let { referringPhysician!!.physicianName = it }
            rp.institutionName?.let { referringPhysician!!.institutionName = it }
            rp.institutionAddress?.let { referringPhysician!!.institutionAddress = it }
            rp.departmentName?.let { referringPhysician!!.departmentName = it }
            rp.physicianAddress?.let { referringPhysician!!.physicianAddress = it }
            rp.physicianTelephone?.let { referringPhysician!!.physicianTelephone = it }
        }
    }
}