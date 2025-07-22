package chloe.sprout.backend.controller

import chloe.sprout.backend.annotation.CustomWebMvcTest
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.UserLoginRequest
import chloe.sprout.backend.dto.UserLoginResponse
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.exception.global.GlobalErrorCode
import chloe.sprout.backend.exception.user.InvalidPasswordException
import chloe.sprout.backend.exception.user.UserAlreadyExistsException
import chloe.sprout.backend.exception.user.UserErrorCode
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.service.UserService
import chloe.sprout.backend.util.TestUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
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

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = User(
            email = "test@test.com",
            name = "name",
            password = "password"
        )
        TestUtils.setSuperClassPrivateField(testUser, "id", UUID.fromString("99999999-9999-9999-9999-999999999999"))
    }

    @Test
    @DisplayName("POST /api/users/signup(회원가입) - 성공")
    fun signupApi_success() {
        // given
        val request = UserSignupRequest(testUser.email, testUser.password, testUser.name)
        val response = UserSignupResponse(testUser.id, testUser.email, testUser.name)

        every { userService.signup(any()) } returns response

        // when & then
        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.data.id") { value(testUser.id.toString()) }
            jsonPath("$.data.email") { value(testUser.email) }
            jsonPath("$.data.name") { value(testUser.name) }
        }

        verify(exactly = 1) { userService.signup(request) }
    }

    @Test
    @DisplayName("POST /api/users/signup(회원가입) - 실패 (잘못된 요청 데이터)")
    fun signupApi_fail_invalidRequest() {
        // given
        val invalidRequest = UserSignupRequest("not-an-email", testUser.password, "")
        val errorDetail = GlobalErrorCode.INVALID_FIELD_VALUE.getErrorDetail()

        // when & then
        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidRequest)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code)}
            jsonPath("$.message") { value(errorDetail.message)}
        }

        verify(exactly = 0) { userService.signup(invalidRequest) }
    }

    @Test
    @DisplayName("POST /api/users/signup(회원가입) - 실패 (이미 존재하는 이메일")
    fun signupApi_fail_emailAlreadyExists() {
        // given
        val request = UserSignupRequest(testUser.email, testUser.password, testUser.name)
        val errorDetail = UserErrorCode.USER_ALREADY_EXISTS.getErrorDetail()

        every { userService.signup(any()) } throws UserAlreadyExistsException()

        mockMvc.post("/api/users/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { userService.signup(request) }
    }

    @Test
    @DisplayName("POST /api/users/login (로그인) - 성공")
    fun loginApi_success() {
        // given
        val request = UserLoginRequest(testUser.email, testUser.password)
        val response = UserLoginResponse(testUser.id, testUser.email, testUser.name)

        every { userService.login(any(), any(), any()) } returns response

        // when & then
        mockMvc.post("/api/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.id") { value(testUser.id.toString()) }
            jsonPath("$.data.email") { value(testUser.email) }
            jsonPath("$.data.name") { value(testUser.name) }
        }

        verify(exactly = 1) { userService.login(request, any(), any()) }
    }

    @Test
    @DisplayName("POST /api/users/login (로그인) - 실패 (사용자 없음)")
    fun loginApi_fail_userNotFound() {
        // given
        val request = UserLoginRequest(testUser.email, testUser.password)
        val errorDetail = UserErrorCode.USER_NOT_FOUND.getErrorDetail()

        every { userService.login(any(), any(), any()) } throws UserNotFoundException()

        // when & then
        mockMvc.post("/api/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { userService.login(request, any(), any()) }
    }

    @Test
    @DisplayName("POST /api/users/login (로그인) - 실패 (비밀번호 불일치)")
    fun loginApi_fail_invalidPassword() {
        // given
        val request = UserLoginRequest(testUser.email, testUser.password)
        val errorDetail = UserErrorCode.INVALID_PASSWORD.getErrorDetail()

        every { userService.login(any(), any(), any()) } throws InvalidPasswordException()

        // when & then
        mockMvc.post("/api/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(errorDetail.status) }
            jsonPath("$.code") { value(errorDetail.code) }
            jsonPath("$.message") { value(errorDetail.message) }
        }

        verify(exactly = 1) { userService.login(request, any(), any()) }
    }

    @Test
    @DisplayName("POST /api/users/refresh (토큰 재발급) - 성공")
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
    @DisplayName("POST /api/users/logout (로그아웃) - 성공")
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