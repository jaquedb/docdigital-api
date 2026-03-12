package com.docdigital.api.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(

    @Value("\${jwt.secret}")
    private val secret: String

) {

    private fun getSignKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(email: String): String {

        val now = Date()
        val expiration = Date(now.time + 1000L * 60 * 60 * 24 * 365) // 1 ano

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(getSignKey())
            .compact()
    }

    fun extractUsername(token: String): String? {
        return extractAllClaims(token).subject
    }

    fun isTokenValid(token: String): Boolean {
        val claims = extractAllClaims(token)
        return !claims.expiration.before(Date())
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .body
    }
}