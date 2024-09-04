package hse.cs.se.study.storage.data.model.web

import java.time.LocalDate
import java.time.LocalTime

class SeriesList(
    val series: List<SeriesId>
) {
    data class SeriesId(
        val seriesUid: String,
        val seriesNumber: Int? = null,
        val seriesDate: LocalDate? = null,
        val seriesTime: LocalTime? = null,
        val modality: String? = null
    )
}