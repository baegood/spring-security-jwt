package com.baegood.crazingspring.security

import com.baegood.crazingspring.security.jwt.JwtFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * api 요청시 사용되는 인증 필터
 */
class RequestAuthenticationProcessingFilter(
    matcher: RequestMatcher,
    private val authenticationFailureHandler: AuthenticationFailureHandler,
    private val jwtFactory: JwtFactory
) : AbstractAuthenticationProcessingFilter(matcher) {

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val token = jwtFactory.extract(request)
        return this.authenticationManager.authenticate(AuthenticationToken(token))
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        this.setNewContext(authResult)
        chain.doFilter(request, response)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?) {
        SecurityContextHolder.clearContext()
        this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed)
    }

    private fun setNewContext(authResult: Authentication) {
        val contextHolder = SecurityContextHolder.createEmptyContext()
        contextHolder.authentication = authResult

        SecurityContextHolder.setContext(contextHolder)
    }
}
