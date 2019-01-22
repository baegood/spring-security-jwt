package com.baegood.crazingspring.security

import com.baegood.crazingspring.domains.AccountPrincipal
import com.baegood.crazingspring.security.jwt.JwtFactory
import com.baegood.crazingspring.services.AccountService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

@Component
class RequestAuthenticationProvider(
    private val accountService: AccountService,
    private val jwtFactory: JwtFactory
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication.credentials as String
        val email = jwtFactory.emailFromClaims(token)
        val accountPrincipal = accountService.loadUserByUsername(email) as AccountPrincipal

        return AuthenticationToken(accountPrincipal, mutableSetOf())
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return AuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
