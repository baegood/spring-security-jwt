package com.baegood.crazingspring.security

import com.baegood.crazingspring.exceptions.AuthenticationFailureException
import com.baegood.crazingspring.requests.LoginRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import java.io.BufferedReader
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 로그인 시 사용되는 인증 필터
 */
class LoginAuthenticationProcessingFilter(
    defaultFilterProcessesUrl: String,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
    private val authenticationFailureHandler: AuthenticationFailureHandler,
    private val objectMapper: ObjectMapper
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val loginRequest = this.getLoginRequest(request.reader)

        return this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password))
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
        SecurityContextHolder.clearContext()
        this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed)
    }

    private fun getLoginRequest(data: BufferedReader): LoginRequest {
        Assert.notNull(data, "데이터가 없습니다")

        val loginRequest = this.objectMapper.readValue(data, LoginRequest::class.java)

        if (StringUtils.isEmpty(loginRequest.email) || StringUtils.isEmpty(loginRequest.password)) {
            throw AuthenticationFailureException("로그인 요청이 잘못 되었습니다.")
        }

        return loginRequest
    }
}
