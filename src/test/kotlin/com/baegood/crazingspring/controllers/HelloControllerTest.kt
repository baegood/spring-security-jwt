package com.baegood.crazingspring.controllers

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@RunWith(SpringRunner::class)
class HelloControllerTest {

    @get:Rule
    val restDocumentation = JUnitRestDocumentation()

    private lateinit var mockMvc: MockMvc

    @Before
    fun setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(HelloController::class.java)
            .apply<StandaloneMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
            .build()
    }

    @Test
    fun `헬로 테스트`() {
        mockMvc
            .perform(RestDocumentationRequestBuilders.get("/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(MockMvcRestDocumentation.document("hello/index"))
    }

    @Test
    fun `헬로 유저 테스트`() {
        val name = "배군"

        mockMvc
            .perform(RestDocumentationRequestBuilders.get("/hello/{name}", name))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
            .andDo(MockMvcRestDocumentation.document("hello/with-name"))
    }
}
