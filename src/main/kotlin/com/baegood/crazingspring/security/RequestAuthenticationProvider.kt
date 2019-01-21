package com.baegood.crazingspring.security

import com.baegood.crazingspring.domains.Account
import com.baegood.crazingspring.domains.AccountContext
import com.baegood.crazingspring.services.AccountService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
class RequestAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val accountService: AccountService
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        Assert.notNull(authentication, "인증 정보가 제공되지 않았습니다.")

        val email = authentication.principal as String
        val password = authentication.credentials as String
        val account = this.accountService.findByEmail(email)

        if (!this.passwordEncoder.matches(password, account.password)) {
            throw BadCredentialsException("비밀번호가 틀렸습니다.")
        }

        val accountContext = AccountContext(account)

        return UsernamePasswordAuthenticationToken(accountContext, null, accountContext.authorities)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
