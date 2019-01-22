package com.baegood.crazingspring.controllers

import com.baegood.crazingspring.requests.AccountRequest
import com.baegood.crazingspring.services.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping
    fun makeAccount(@RequestBody accountRequest: AccountRequest): ResponseEntity<*> {
        return ResponseEntity.ok(accountService.createAccount(accountRequest))
    }
}
