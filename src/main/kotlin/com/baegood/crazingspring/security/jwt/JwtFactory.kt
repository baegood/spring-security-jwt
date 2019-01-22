package com.baegood.crazingspring.security.jwt

import com.baegood.crazingspring.exceptions.JsonWebTokenException
import com.baegood.crazingspring.properties.JsonWebTokenProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.micrometer.core.instrument.util.StringUtils
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.servlet.http.HttpServletRequest

@Component
class JwtFactory(
    private val jsonWebTokenProperties: JsonWebTokenProperties
) {

    private val currentTime: LocalDateTime
        get() = LocalDateTime.now()

    private val issuedAt: Date
        get() = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant())

    private val accessTokenExpiration: Date
        get() = getExpiration(this.jsonWebTokenProperties.expriration)

    fun createAccessToken(email: String, authorities: MutableCollection<out GrantedAuthority>): AccessToken {
        val claims = Jwts.claims().setSubject(email)
        claims["scopes"] = authorities.map { grantedAuthority -> grantedAuthority.toString() }

        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(this.jsonWebTokenProperties.issuer)
            .setIssuedAt(this.issuedAt)
            .setExpiration(this.accessTokenExpiration)
            .signWith(SignatureAlgorithm.HS512, jsonWebTokenProperties.signingKey)
            .compact()

        return AccessToken(token, claims)
    }

    fun extract(request: HttpServletRequest): String {
        val header = request.getHeader(jsonWebTokenProperties.headerPrefix)
        if (StringUtils.isBlank(header) || !header.startsWith(jsonWebTokenProperties.headerPrefix)) {
            throw JsonWebTokenException("인증 헤더가 없습니다.")
        }

        return header.substring(jsonWebTokenProperties.headerPrefix.length)
    }

    fun parse(token: String): Jws<Claims> {
        return Jwts
            .parser()
            .setSigningKey(jsonWebTokenProperties.signingKey)
            .parseClaimsJws(token)
    }

    fun emailFromClaims(token: String): String {
        return parse(token).body.subject
    }

    private fun getExpiration(expirationSeconds: Int): Date {
        return Date.from(
            currentTime
                .plusSeconds(expirationSeconds.toLong())
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )
    }
}
