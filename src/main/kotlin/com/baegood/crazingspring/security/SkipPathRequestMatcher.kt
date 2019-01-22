package com.baegood.crazingspring.security

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.util.Assert
import javax.servlet.http.HttpServletRequest

class SkipPathRequestMatcher(
    pathsToSkip: List<String>,
    processingPath: String
) : RequestMatcher {

    private val matchers: OrRequestMatcher
    private val processingMatcher: RequestMatcher

    init {
        Assert.notNull(pathsToSkip, "스킵 패스가 없습니다")

        this.matchers = OrRequestMatcher(
            pathsToSkip.map { AntPathRequestMatcher(it) }
        )

        this.processingMatcher = AntPathRequestMatcher(processingPath)
    }

    override fun matches(request: HttpServletRequest?): Boolean {
        if (matchers.matches(request)) {
            return false
        }

        return processingMatcher.matches(request)
    }
}
