package com.baegood.crazingspring.config

import com.baegood.crazingspring.properties.CorsProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val corsProperties: CorsProperties
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        val source = UrlBasedCorsConfigurationSource()

        config.allowedOrigins = corsProperties.origins
        config.allowedHeaders = corsProperties.headers
        config.allowedMethods = corsProperties.methods

        source.registerCorsConfiguration(corsProperties.path, config)

        return source
    }

    override fun configure(http: HttpSecurity) {
        http
            .cors().and()
            .csrf().and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/hello/**").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
            .anyRequest().authenticated()
    }
}
