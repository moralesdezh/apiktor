package com.example.repository

import com.example.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Users : Table("users") {
    val id = uuid("id").clientDefault { UUID.randomUUID() } // UUID generado en el código
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20)
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() } // Fecha generada en el código
}


class UserRepository {

    fun findAll(): List<User> = transaction {
        Users.selectAll().map {
            User(
                id = it[Users.id],
                username = it[Users.username],
                email = it[Users.email],
                passwordHash = it[Users.passwordHash],
                role = it[Users.role],
                createdAt = it[Users.createdAt]
            )
        }
    }

    fun findById(id: UUID): User? = transaction {
        Users.select { Users.id eq id }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash],
                    role = it[Users.role],
                    createdAt = it[Users.createdAt]
                )
            }
            .singleOrNull()
    }

    fun findByUsername(username: String): User? = transaction {
        Users.select { Users.username eq username }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash],
                    role = it[Users.role],
                    createdAt = it[Users.createdAt]
                )
            }
            .singleOrNull()
    }

    fun save(user: User): Boolean = transaction {
        val result = Users.insert {
            it[id] = user.id               // UUID generado en el código
            it[username] = user.username
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[role] = user.role
            it[createdAt] = user.createdAt // Fecha generada en el código
        }
        result.insertedCount > 0
    }
}