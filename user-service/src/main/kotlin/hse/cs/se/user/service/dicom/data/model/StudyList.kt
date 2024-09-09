package hse.cs.se.user.service.dicom.data.model

import java.time.LocalDate
import java.time.LocalTime

class StudyList(
    val studies: List<StudyId>
) {
    data class StudyId(
        val studyUid: String,
        val studyId: String? = null,
        val studyDate: LocalDate? = null,
        val studyTime: LocalTime? = null
    )
}