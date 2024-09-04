package hse.cs.se.study.storage.data.repository

import hse.cs.se.study.storage.data.model.DicomInstance
import hse.cs.se.study.storage.data.model.Series
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface DicomInstanceRepository : CrudRepository<DicomInstance, UUID> {

    fun findByS3DicomFilePath(s3DicomFilePath: String): DicomInstance?

    fun findAllBySeriesUid(seriesUid: String): List<DicomInstance>

    fun existsBySeriesUid(seriesUid: String): Boolean
}