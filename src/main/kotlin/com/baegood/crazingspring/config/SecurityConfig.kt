package com.baegood.crazingspring.config

import com.baegood.crazingspring.properties.CorsProperties
import com.baegood.crazingspring.security.LoginAuthenticationProcessingFilter
import com.baegood.crazingspring.security.LoginAuthenticationProvider
import com.baegood.crazingspring.security.RequestAuthenticationProcessingFilter
import com.baegood.crazingspring.security.RequestAuthenticationProvider
import com.baegood.crazingspring.security.SkipPathRequestMatcher
import com.baegood.crazingspring.security.jwt.JwtFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val corsProperties: CorsProperties,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val successHandler: AuthenticationSuccessHandler,
    private val failureHandler: AuthenticationFailureHandler,
    private val loginAuthenticationProvider: LoginAuthenticationProvider,
    private val requestAuthenticationProvider: RequestAuthenticationProvider,
    private val jwtFactory: JwtFactory,
    private val objectMapper: ObjectMapper
) : WebSecurityConfigurerAdapter() {

    companion object {
        const val LOGIN_ENTRY_POINT = "/auth/login"
        const val JOIN_ENTRY_POINT = "/accounts"
        const val AUTH_ENTRY_POINT = "/**"
    }

    private fun buildLoginProcessingFilter(): LoginAuthenticationProcessingFilter {
        val filter = LoginAuthenticationProcessingFilter(LOGIN_ENTRY_POINT, this.successHandler, this.failureHandler, this.objectMapper)

        filter.setAuthenticationManager(this.authenticationManagerBean())

        return filter
    }

//    private fun buildRequestProcessingFilter(): RequestAuthenticationProcessingFilter {
//        val pathsToSkip = listOf(LOGIN_ENTRY_POINT, AUTH_ENTRY_POINT, JOIN_ENTRY_POINT)
//        val matcher = SkipPathRequestMatcher(pathsToSkip, AUTH_ENTRY_POINT)
//        val filter = RequestAuthenticationProcessingFilter(matcher, this.failureHandler, this.jwtFactory)
//
//        filter.setAuthenticationManager(this.authenticationManagerBean())
//
//        return filter
//    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(this.loginAuthenticationProvider)
        auth.authenticationProvider(this.requestAuthenticationProvider)
    }


    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        val source = UrlBasedCorsConfigurationSource()

        config.allowedOrigins = this.corsProperties.origins
        config.allowedHeaders = this.corsProperties.headers
        config.allowedMethods = this.corsProperties.methods

        source.registerCorsConfiguration(this.corsProperties.path, config)

        return source
    }

    override fun configure(http: HttpSecurity) {
        http
            .cors().and()
            .csrf().and()
            .exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/hello/**").permitAll()
            .antMatchers(HttpMethod.POST, LOGIN_ENTRY_POINT, "/accounts").permitAll()
            .anyRequest().authenticated()

        http
            .addFilterBefore(this.buildLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
//            .addFilterBefore(this.buildRequestProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}
