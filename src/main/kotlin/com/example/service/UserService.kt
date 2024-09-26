package com.example.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.model.User
import com.example.repository.RefreshTokenRepository
import com.example.repository.UserRepository
import com.example.routing.request.LoginRequest
import com.example.routing.response.AuthResponse
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    // Encontrar todos los usuarios
    fun findAll(): List<User> = userRepository.findAll()

    // Encontrar un usuario por su UUID
    fun findById(id: UUID): User? = userRepository.findById(id)

    // Encontrar un usuario por su nombre de usuario
    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    // Guardar un nuevo usuario (registrar usuario)
    fun save(user: User): User? {
        val foundUser = findByUsername(user.username)

        return if (foundUser == null) {
            // Hashear la contraseña antes de guardar el usuario
            val hashedPassword = hashPassword(user.passwordHash)
            val userToSave = user.copy(passwordHash = hashedPassword)
            userRepository.save(userToSave)
            userToSave
        } else null
    }

    // Autenticar un usuario con las credenciales de login (username y password)
    fun authenticate(loginRequest: LoginRequest): AuthResponse? {
        val username = loginRequest.username
        val foundUser = userRepository.findByUsername(username)

        // Verificar si el usuario existe y la contraseña es válida
        return if (foundUser != null && verifyPassword(loginRequest.password, foundUser.passwordHash)) {
            // Generar tokens de acceso y refresco
            val accessToken = jwtService.createAccessToken(username)
            val refreshToken = jwtService.createRefreshToken(username)

            // Guardar el refresh token
            refreshTokenRepository.save(refreshToken, username)

            // Devolver la respuesta de autenticación con los tokens
            AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } else null
    }

    // Refrescar el token de acceso usando el refresh token
    fun refreshToken(token: String): String? {
        val decodedRefreshToken = verifyRefreshToken(token)
        val persistedUsername = refreshTokenRepository.findUsernameByToken(token)

        return if (decodedRefreshToken != null && persistedUsername != null) {
            val foundUser = userRepository.findByUsername(persistedUsername)
            val usernameFromRefreshToken = decodedRefreshToken.getClaim("username").asString()

            if (foundUser != null && usernameFromRefreshToken == foundUser.username)
                jwtService.createAccessToken(persistedUsername)
            else
                null
        } else null
    }

    // Método privado para verificar si la contraseña ingresada coincide con el hash almacenado
    private fun verifyPassword(password: String, passwordHash: String): Boolean {
        return BCrypt.checkpw(password, passwordHash)  // Comparar la contraseña en texto plano con el hash
    }

    // Método privado para hashear una contraseña antes de almacenarla
    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())  // Generar el hash de la contraseña usando BCrypt
    }

    // Verificar el token de refresco para asegurarse de que sea válido
    private fun verifyRefreshToken(token: String): DecodedJWT? {
        val decodedJWT = decodedJWT(token)

        return decodedJWT?.let {
            val audienceMatches = jwtService.audienceMatches(it.audience.first())
            if (audienceMatches) decodedJWT else null
        }
    }

    // Decodificar el JWT (token de acceso o refresco)
    private fun decodedJWT(token: String): DecodedJWT? {
        return try {
            jwtService.jwtVerifier.verify(token)
        } catch (ex: Exception) {
            null
        }
    }
}
