package hse.cs.se.study.storage.data.repository

import hse.cs.se.study.storage.data.model.Study
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface StudyRepository : CrudRepository<Study, String> {

    fun existsByPatientUid(patientUid: UUID): Boolean

    fun findAllByPatientUid(patientUid: UUID): List<Study>

    @Query("select patient_uid from study where study_uid = :studyUid")
    fun getPatientUidByStudyUid(studyUid: String): UUID?

}