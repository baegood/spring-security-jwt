package com.baegood.crazingspring.exceptions

import org.springframework.security.core.AuthenticationException

class JsonWebTokenException(message: String) : AuthenticationException(message)
