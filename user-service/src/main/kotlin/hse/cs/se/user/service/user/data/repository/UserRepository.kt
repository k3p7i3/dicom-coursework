package hse.cs.se.user.service.user.data.repository

import hse.cs.se.user.service.user.data.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.security.provisioning.JdbcUserDetailsManager
import java.util.*

interface UserRepository : CrudRepository<User, UUID> {

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean
}