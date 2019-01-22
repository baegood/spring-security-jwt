package com.baegood.crazingspring.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jsonwebtoken")
class JsonWebTokenProperties {

    lateinit var issuer: String
    lateinit var signingKey: String
    lateinit var headerPrefix: String
    lateinit var headerName: String
    var expriration: Int = 0
}
