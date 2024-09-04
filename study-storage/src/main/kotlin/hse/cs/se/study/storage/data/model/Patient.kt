package hse.cs.se.study.storage.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

@Table("patient")
data class Patient(

    @Id
    @Column("patient_uid")
    var patientUid: UUID? = null,

    @Column("patient_id")
    var patientId: String? = null,

    @Column("patient_name")
    var patientName: String? = null,

    @Column("patient_birth_date")
    var patientBirthDate: LocalDate? = null,

    @Column("patient_sex")
    var patientSex: Sex? = null,

    @Column("patient_comments")
    var patientComments: String? = null,

    @Column("domain")
    var domain: String? = null
): Persistable<UUID> {

    override fun getId() = patientUid

    override fun isNew() = (patientUid == null)

    enum class Sex { FEMALE, MALE, OTHER }

    fun updateWith(patient: Patient) {
        patient.patientName?.let { patientName = it }
        patient.patientBirthDate?.let { patientBirthDate = it }
        patient.patientSex?.let { patientSex = it }
        patient.patientComments?.let { patientComments = it }
        patient.domain?.let { domain = it }
    }
}
