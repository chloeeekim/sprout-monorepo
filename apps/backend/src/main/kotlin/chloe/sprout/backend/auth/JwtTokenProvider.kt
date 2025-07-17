package chloe.sprout.backend.auth

import chloe.sprout.backend.property.JwtProperties
import chloe.sprout.backend.service.RedisService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
    private val redisService: RedisService
) {
    private val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateAccessToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.accessExpiration)

        return Jwts.builder().apply {
            setHeader(createHeader())
            setSubject(email)
            setIssuedAt(now)
            setExpiration(expiryDate)
            signWith(key, SignatureAlgorithm.HS256)
        }.compact()
    }

    fun generateRefreshToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.refreshExpiration)

        return Jwts.builder().apply {
            setHeader(createHeader())
            setSubject(email)
            setIssuedAt(now)
            setExpiration(expiryDate)
            signWith(key, SignatureAlgorithm.HS256)
        }.compact()
    }

    fun getEmailFromToken(token: String): String {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject
    }

    fun getExpiration(token: String): Date {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.expiration
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun validateRefreshToken(email: String, token: String): Boolean {
        val storedRefreshToken = redisService.getRefreshToken(email)
        return storedRefreshToken == token && validateToken(token)
    }

    private fun createHeader(): MutableMap<String, Any> {
        val header: MutableMap<String, Any> = HashMap<String, Any>()
        header.put("typ", "JWT")
        header.put("alg", "HS256")
        return header
    }
}