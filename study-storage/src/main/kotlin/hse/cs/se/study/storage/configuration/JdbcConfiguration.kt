package hse.cs.se.study.storage.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import hse.cs.se.study.storage.data.model.Series
import hse.cs.se.study.storage.data.model.Study
import org.postgresql.util.PGobject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class JdbcConfiguration(
    private val objectMapper: ObjectMapper
) : AbstractJdbcConfiguration() {

    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(
            mutableListOf(
                PatientStudyReadingConverter(objectMapper),
                PatientStudyWritingConverter(objectMapper),
                PhysicianReadingConverter(objectMapper),
                PhysicianWritingConverter(objectMapper),
                EquipmentReadingConverter(objectMapper),
                EquipmentWritingConverter(objectMapper),
            )
        )
    }

    @ReadingConverter
    class PatientStudyReadingConverter(
        private val objectMapper: ObjectMapper
    ): Converter<PGobject, Study.PatientStudy> {
        override fun convert(source: PGobject): Study.PatientStudy {
            return objectMapper.readValue(
                source.value,
                Study.PatientStudy::class.java
            )
        }
    }

    @WritingConverter
    class PatientStudyWritingConverter(
        private val objectMapper: ObjectMapper
    ): Converter<Study.PatientStudy, PGobject> {
        override fun convert(source: Study.PatientStudy): PGobject {
            val jsonObj = PGobject()
            jsonObj.type = "json"
            jsonObj.value = objectMapper.writeValueAsString(source)
            return jsonObj
        }
    }

    @ReadingConverter
    class PhysicianReadingConverter(
        private val objectMapper: ObjectMapper
    ): Converter<PGobject, Study.Physician> {
        override fun convert(source: PGobject): Study.Physician {
            return objectMapper.readValue(
                source.value,
                Study.Physician::class.java
            )
        }
    }

    @WritingConverter
    class PhysicianWritingConverter(
        private val objectMapper: ObjectMapper
    ): Converter<Study.Physician, PGobject> {
        override fun convert(source: Study.Physician): PGobject {
            val jsonObj = PGobject()
            jsonObj.type = "json"
            jsonObj.value = objectMapper.writeValueAsString(source)
            return jsonObj
        }
    }

    @ReadingConverter
    class EquipmentReadingConverter(
        private val objectMapper: ObjectMapper
    ): Converter<PGobject, Series.Equipment> {
        override fun convert(source: PGobject): Series.Equipment {
            return objectMapper.readValue(
                source.value,
                Series.Equipment::class.java
            )
        }
    }

    @WritingConverter
    class EquipmentWritingConverter(
        private val objectMapper: ObjectMapper
    ): Converter<Series.Equipment, PGobject> {
        override fun convert(source: Series.Equipment): PGobject {
            val jsonObj = PGobject()
            jsonObj.type = "json"
            jsonObj.value = objectMapper.writeValueAsString(source)
            return jsonObj
        }
    }
}