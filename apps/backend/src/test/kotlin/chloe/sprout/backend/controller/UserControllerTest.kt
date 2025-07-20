package chloe.sprout.backend.controller

import chloe.sprout.backend.annotation.CustomWebMvcTest
import chloe.sprout.backend.auth.JwtAuthenticationFilter
import chloe.sprout.backend.config.SecurityConfig
import chloe.sprout.backend.dto.UserLoginRequest
import chloe.sprout.backend.dto.UserLoginResponse
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.exception.user.UserAlreadyExistsException
import chloe.sprout.backend.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.*

@CustomWebMvcTest(controllers = [UserController::class])
class UserControllerTest{

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var userService: UserService

    @Test
    @DisplayName("회원가입 API - 성공")
    fun signupApi_success() {
        // given
        val request = UserSignupRequest("test@test.com", "password", "name")
        val response = UserSignupResponse(UUID.randomUUID(), request.email, request.name)

        every { userService.signup(any()) } returns response

        // when & then
        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.data.id") { exists() }
            jsonPath("$.data.email") { value(request.email) }
            jsonPath("$.data.name") { value(request.name) }
        }

        verify(exactly = 1) { userService.signup(any()) }
    }

    @Test
    @DisplayName("회원가입 API - 실패 (잘못된 요청 데이터)")
    fun signupApi_failure_invalidRequest() {
        // given
        val invalidRequest = UserSignupRequest("not-an-email", "password", "")

        // when & then
        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { userService.signup(any()) }
    }

    @Test
    @DisplayName("회원가입 API - 실패 (이미 존재하는 이메일")
    fun signupApi_failure_emailAlreadyExists() {
        // given
        val request = UserSignupRequest("test@test.com", "password", "name")

        every { userService.signup(any()) } throws UserAlreadyExistsException()

        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isConflict() }
        }

        verify(exactly = 1) { userService.signup(any()) }
    }

    @Test
    @DisplayName("로그인 API - 성공")
    fun loginApi_success() {
        // given
        val request = UserLoginRequest("test@test.com", "password")
        val response = UserLoginResponse(UUID.randomUUID(), request.email, "name")

        every { userService.login(any(), any(), any()) } returns response

        // when & then
        mockMvc.post("/api/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.id") { exists() }
            jsonPath("$.data.email") { value(request.email) }
            jsonPath("$.data.name") { value("name")}
        }

        verify(exactly = 1) { userService.login(any(), any(), any()) }
    }

    @Test
    @DisplayName("토큰 재발급 API - 성공")
    fun refreshApi_success() {
        // given
        val refreshToken = "refreshToken"
        every { userService.refresh(any(), any()) } returns Unit

        // when & then
        mockMvc.post("/api/users/refresh") {
            cookie(Cookie("refreshToken", refreshToken))
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { userService.refresh(any(), any()) }
    }

    @Test
    @DisplayName("로그아웃 API - 성공")
    fun logoutApi_success() {
        // given
        val accessToken = "accessToken"
        every { userService.logout(any()) } returns Unit

        // when & then
        mockMvc.post("/api/users/logout") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { userService.logout(any()) }
    }
}