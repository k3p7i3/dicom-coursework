package hse.cs.se.study.storage.data.repository

import hse.cs.se.study.storage.data.model.Patient
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface PatientRepository : CrudRepository<Patient, UUID> {

    fun findByPatientId(patientId: String): Patient?

    fun findAllByDomain(domain: String): List<Patient>
}