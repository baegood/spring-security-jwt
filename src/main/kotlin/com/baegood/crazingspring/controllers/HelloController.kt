package com.baegood.crazingspring.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping
    fun hello(): String {
        return "Hello!"
    }

    @GetMapping("/{name}")
    fun helloUser(@PathVariable name: String): String {
        return "Hello! $name"
    }
}
