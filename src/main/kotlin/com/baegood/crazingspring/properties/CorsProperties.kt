package com.baegood.crazingspring.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cors")
class CorsProperties {

    lateinit var path: String
    lateinit var methods: List<String>
    lateinit var headers: List<String>
    lateinit var origins: List<String>
}
