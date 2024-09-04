package hse.cs.se.study.storage.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalTime

@Table("series")
data class Series(
    @Id
    @Column("series_uid")
    var seriesUid: String,

    @Column("study_uid")
    var studyUid: String,

    @Column("series_number")
    var seriesNumber: Int? = null,

    @Column("modality")
    var modality: String? = null,

    @Column("series_date")
    var seriesDate: LocalDate? = null,

    @Column("series_time")
    var seriesTime: LocalTime? = null,

    @Column("series_description")
    var seriesDescription: String? = null,

    @Column("performing_physician_name")
    var performingPhysicianName: String? = null,

    @Column("operator_name")
    var operatorName: String? = null,

    @Column("body_part_examined")
    var bodyPartExamined: String? = null,

    @Column("equipment")
    var equipment: Equipment? = null
): Persistable<String> {

    override fun getId() = seriesUid

    override fun isNew() = true

    data class Equipment(
        var manufacturer: String? = null,
        var institutionName: String? = null,
        var institutionAddress: String? = null,
        var stationName: String? = null,
        var institutionalDepartmentName: String? = null
    )

    fun updateWith(series: Series) {
        series.seriesNumber?.let { seriesNumber = it}
        series.modality?.let { modality = it}
        series.seriesDate?.let { seriesDate = it}
        series.seriesTime?.let { seriesTime = it}
        series.seriesDescription?.let { seriesDescription = it}
        series.performingPhysicianName?.let { performingPhysicianName = it}
        series.operatorName?.let { operatorName = it}
        series.bodyPartExamined?.let { bodyPartExamined = it}

        series.equipment?.let { eq ->

            if (equipment == null) equipment = Equipment()

            eq.manufacturer?.let { equipment!!.manufacturer = it}
            eq.institutionName?.let { equipment!!.institutionName = it}
            eq.institutionAddress?.let { equipment!!.institutionAddress = it}
            eq.stationName?.let { equipment!!.stationName = it}
            eq.institutionalDepartmentName?.let { equipment!!.institutionalDepartmentName = it}
        }
    }
}