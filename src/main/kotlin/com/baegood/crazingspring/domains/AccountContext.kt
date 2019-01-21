package com.baegood.crazingspring.domains

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AccountContext(
    val account: Account,
    private val authorities: MutableSet<GrantedAuthority> = mutableSetOf()
) : UserDetails {

    override fun getUsername(): String {
        return account.email
    }

    override fun getPassword(): String {
        return account.password
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}
