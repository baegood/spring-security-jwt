package com.baegood.crazingspring.security

import com.baegood.crazingspring.domains.AccountPrincipal
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class AuthenticationToken : AbstractAuthenticationToken {

    private var token: String? = null
    private var accountPrincipal: AccountPrincipal? = null

    constructor(token: String): super(null) {
        this.token = token
        this.isAuthenticated = false
    }

    constructor(accountPrincipal: AccountPrincipal, authorities: MutableSet<out GrantedAuthority>): super(authorities) {
        this.eraseCredentials()
        this.accountPrincipal = accountPrincipal
        this.isAuthenticated = true
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
        }

        super.setAuthenticated(authenticated)
    }

    override fun getCredentials(): Any {
        return this.token!!
    }

    override fun getPrincipal(): Any {
        return this.accountPrincipal!!
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        this.token = null
    }
}
