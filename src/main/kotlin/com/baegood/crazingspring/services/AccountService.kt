package com.baegood.crazingspring.services

import com.baegood.crazingspring.domains.Account
import com.baegood.crazingspring.domains.AccountPrincipal
import com.baegood.crazingspring.domains.AccountRepository
import com.baegood.crazingspring.requests.AccountRequest
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val account = accountRepository.findByEmail(username).orElseThrow {
            UsernameNotFoundException("존재하지 않는 사용자 입니다. ($username)")
        }

        return AccountPrincipal(account)
    }

    fun findByEmail(email: String): Account {
        return accountRepository.findByEmail(email).orElseThrow {
            EntityNotFoundException("이메일을 찾을 수 없습니다. ($email)")
        }
    }

    fun createAccount(body: AccountRequest): Account {
        val account = Account(
            body.email,
            passwordEncoder.encode(body.password),
            body.name
        )

        return accountRepository.save(account)
    }
}
