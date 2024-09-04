package hse.cs.se.study.storage.data.model.web

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