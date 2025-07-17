package chloe.sprout.backend.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,
    @Value("\${jwt.expiration-ms}")
    private val jwtExpirationMs: Long
) {
    private val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

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

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            false
        }
    }

    private fun createHeader(): MutableMap<String, Any> {
        val header: MutableMap<String, Any> = HashMap<String, Any>()
        header.put("typ", "JWT")
        header.put("alg", "HS256")
        return header
    }
}