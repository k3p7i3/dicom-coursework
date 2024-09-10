package hse.cs.se.user.service.user.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

@Table("user_table")
data class User(
    @Id
    @Column("id")
    var uid: UUID? = null,

    @Column("first_name")
    var firstName: String,

    @Column("last_name")
    var lastName: String,

    @Column("email")
    var email: String,

    @Column("password")
    var userPassword: String
) : Persistable<UUID>, UserDetails {

    override fun getId() = uid

    override fun isNew() = (uid == null)

    override fun getUsername() = email

    override fun getPassword() = userPassword

    override fun getAuthorities() =
        listOf(SimpleGrantedAuthority("ROLE_USER"))
}