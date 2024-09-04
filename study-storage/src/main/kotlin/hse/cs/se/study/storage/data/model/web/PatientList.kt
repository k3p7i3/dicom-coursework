package hse.cs.se.study.storage.data.model.web

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