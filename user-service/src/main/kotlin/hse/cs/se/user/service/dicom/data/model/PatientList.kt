package hse.cs.se.user.service.dicom.data.model

import java.util.UUID

data class PatientList(
    val patients: List<PatientId>
) {
    data class PatientId(
        val patientUid: UUID,
        val patientId: String,
        val patientName: String? = null
    )
}