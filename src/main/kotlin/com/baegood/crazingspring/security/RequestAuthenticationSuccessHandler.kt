package com.baegood.crazingspring.security

import com.baegood.crazingspring.domains.AccountContext
import com.baegood.crazingspring.responses.TokenResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RequestAuthenticationSuccessHandler(
    private val objectMapper: ObjectMapper
) : AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val accountContext = authentication.principal as AccountContext
        val accessToken = generateAccessToken(accountContext.username)

        response.status = HttpServletResponse.SC_OK
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(objectMapper.writeValueAsString(accessToken))
    }

    private fun generateAccessToken(email: String): TokenResponse {
        return TokenResponse("lfjwgiewiuida$email")
    }
}
