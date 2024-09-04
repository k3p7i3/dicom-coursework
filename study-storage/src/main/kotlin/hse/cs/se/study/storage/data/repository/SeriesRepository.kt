package hse.cs.se.study.storage.data.repository

import hse.cs.se.study.storage.data.model.Series
import hse.cs.se.study.storage.data.model.Study
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SeriesRepository : CrudRepository<Series, String> {

    fun existsByStudyUid(studyUid: String): Boolean

    fun findAllByStudyUid(studyUid: String): List<Series>

    @Query("select study_uid from series where series_uid = :seriesUid")
    fun getStudyUidBySeriesUid(seriesUid: String): String?
}