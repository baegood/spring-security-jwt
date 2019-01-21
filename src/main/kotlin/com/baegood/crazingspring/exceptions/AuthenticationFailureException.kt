package com.baegood.crazingspring.exceptions

import org.springframework.security.core.AuthenticationException

class AuthenticationFailureException(message: String) : AuthenticationException(message)
