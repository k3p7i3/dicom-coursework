package hse.cs.se.study.storage.data.repository

import hse.cs.se.study.storage.data.model.Series
import hse.cs.se.study.storage.data.model.Study
import org.springframework.data.jdbc.core.JdbcAggregateTemplate

class UpdateEntityRepository(
    private val template: JdbcAggregateTemplate
) {

    fun updateStudy(study: Study): Study =
        template.update(study)

    fun updateSeries(series: Series): Series =
        template.update(series)
}