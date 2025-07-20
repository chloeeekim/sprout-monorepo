
package chloe.sprout.backend.service

import chloe.sprout.backend.auth.JwtTokenProvider
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.UserLoginRequest
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.exception.auth.InvalidRefreshTokenException
import chloe.sprout.backend.exception.auth.MissingRefreshTokenException
import chloe.sprout.backend.exception.user.InvalidPasswordException
import chloe.sprout.backend.exception.user.UserAlreadyExistsException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockK
    private lateinit var redisService: RedisService

    @InjectMockKs
    private lateinit var userService: UserService

    @Test
    @DisplayName("회원가입 - 성공")
    fun signup_success() {
        // given
        val request = UserSignupRequest("test@test.com", "password", "name")
        val encodedPassword = "encodedPassword"
        val user = User(request.email, encodedPassword, request.name)

        every { userRepository.findByEmail(request.email) } returns null
        every { passwordEncoder.encode(request.password) } returns encodedPassword
        every { userRepository.save(any()) } returns user

        // when
        val response = userService.signup(request)

        // then
        assertThat(response.email).isEqualTo(request.email)
        assertThat(response.name).isEqualTo(request.name)

        verify(exactly = 1) { userRepository.findByEmail(request.email) }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("회원가입 - 실패 (이메일 중복)")
    fun signup_fail_emailAlreadyExists() {
        // given
        val request = UserSignupRequest("test@test.com", "password", "name")
        val existingUser = User(request.email, "password", "name")

        every { userRepository.findByEmail(request.email) } returns existingUser

        // when & then
        assertThrows(UserAlreadyExistsException::class.java) {
            userService.signup(request)
        }

        verify(exactly = 1) { userRepository.findByEmail(request.email) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("로그인 - 성공")
    fun login_success() {
        // given
        val request = UserLoginRequest("test@test.com", "password")
        val user = User(request.email, "encodedPassword", "name")
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val httpServletResponse = MockHttpServletResponse()

        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns true
        every { jwtTokenProvider.generateAccessToken(user.email) } returns accessToken
        every { jwtTokenProvider.generateRefreshToken(user.email) } returns refreshToken
        every { redisService.saveRefreshToken(user.email, refreshToken) } returns Unit

        // when
        val response = userService.login(request, mockk(), httpServletResponse)

        // then
        assertThat(response.email).isEqualTo(user.email)
        assertThat(response.name).isEqualTo(user.name)

        assertThat(httpServletResponse.getHeader("Authorization")).isEqualTo("Bearer $accessToken")

        val responseCookie = httpServletResponse.getCookie("refreshToken")
        assertThat(responseCookie).isNotNull
        assertThat(responseCookie?.value).isEqualTo(refreshToken)
        assertThat(responseCookie?.isHttpOnly).isTrue
        assertThat(responseCookie?.secure).isTrue
        assertThat(responseCookie?.path).isEqualTo("/")
        assertThat(responseCookie?.maxAge).isEqualTo(1209600) // 14 days

        verify(exactly = 1) { userRepository.findByEmail(request.email) }
        verify(exactly = 1) { passwordEncoder.matches(request.password, user.password) }
        verify(exactly = 1) { jwtTokenProvider.generateAccessToken(user.email) }
        verify(exactly = 1) { jwtTokenProvider.generateRefreshToken(user.email) }
        verify(exactly = 1) { redisService.saveRefreshToken(user.email, refreshToken) }
    }

    @Test
    @DisplayName("로그인 - 실패 (사용자 없음)")
    fun login_fail_userNotFound() {
        // given
        val request = UserLoginRequest("test@test.com", "password")

        every { userRepository.findByEmail(request.email) } returns null

        // when & then
        assertThrows(UserNotFoundException::class.java) {
            userService.login(request, mockk(), mockk())
        }

        verify(exactly = 1) { userRepository.findByEmail(request.email) }
    }

    @Test
    @DisplayName("로그인 - 실패 (비밀번호 불일치)")
    fun login_fail_invalidPassword() {
        // given
        val request = UserLoginRequest("test@test.com", "password")
        val user = User(request.email, "encodedPassword", "name")

        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns false

        // when & then
        assertThrows(InvalidPasswordException::class.java) {
            userService.login(request, mockk(), mockk())
        }

        verify(exactly = 1) { userRepository.findByEmail(request.email) }
        verify(exactly = 1) { passwordEncoder.matches(request.password, user.password) }
    }

    @Test
    @DisplayName("토큰 재발급 - 성공")
    fun refresh_success() {
        // given
        val refreshToken = "validRefreshToken"
        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"
        val email = "test@test.com"

        val request = mockk<HttpServletRequest>()
        val response = MockHttpServletResponse()
        val cookie = Cookie("refreshToken", refreshToken)

        every { request.cookies } returns arrayOf(cookie)
        every { jwtTokenProvider.getEmailFromToken(refreshToken) } returns email
        every { jwtTokenProvider.validateRefreshToken(email, refreshToken) } returns true
        every { jwtTokenProvider.generateAccessToken(email) } returns newAccessToken
        every { jwtTokenProvider.generateRefreshToken(email) } returns newRefreshToken
        every { redisService.saveRefreshToken(email, newRefreshToken) } returns Unit

        // when
        userService.refresh(request, response)

        // then
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer $newAccessToken")

        val responseCookie = response.getCookie("refreshToken")
        assertThat(responseCookie).isNotNull
        assertThat(responseCookie?.value).isEqualTo(newRefreshToken)
        assertThat(responseCookie?.isHttpOnly).isTrue
        assertThat(responseCookie?.secure).isTrue
        assertThat(responseCookie?.path).isEqualTo("/")
        assertThat(responseCookie?.maxAge).isEqualTo(1209600) // 14 days

        verify(exactly = 1) { jwtTokenProvider.getEmailFromToken(refreshToken) }
        verify(exactly = 1) { jwtTokenProvider.validateRefreshToken(email, refreshToken) }
        verify(exactly = 1) { jwtTokenProvider.generateAccessToken(email) }
        verify(exactly = 1) { jwtTokenProvider.generateRefreshToken(email) }
        verify(exactly = 1) { redisService.saveRefreshToken(email, newRefreshToken) }
    }

    @Test
    @DisplayName("토큰 재발급 - 실패 (리프레시 토큰 없음)")
    fun refresh_fail_missingRefreshToken() {
        // given
        val request = mockk<HttpServletRequest>()
        every { request.cookies } returns null

        // when & then
        assertThrows(MissingRefreshTokenException::class.java) {
            userService.refresh(request, mockk())
        }
    }

    @Test
    @DisplayName("토큰 재발급 - 실패 (유효하지 않은 리프레시 토큰")
    fun refresh_fail_invalidRefreshToken() {
        // given
        val refreshToken = "invalidRefreshToken"
        val email = "test@test.com"
        val request = mockk<HttpServletRequest>()
        val cookie = Cookie("refreshToken", refreshToken)

        every { request.cookies } returns arrayOf(cookie)
        every { jwtTokenProvider.getEmailFromToken(refreshToken) } returns email
        every { jwtTokenProvider.validateRefreshToken(email, refreshToken) } returns false

        // when & then
        assertThrows(InvalidRefreshTokenException::class.java) {
            userService.refresh(request, mockk())
        }

        verify(exactly = 1) { jwtTokenProvider.getEmailFromToken(refreshToken) }
        verify(exactly = 1) { jwtTokenProvider.validateRefreshToken(email, refreshToken) }
        verify(exactly = 0) { jwtTokenProvider.generateAccessToken(email) }
        verify(exactly = 0) { jwtTokenProvider.generateRefreshToken(email) }
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    fun logout_success() {
        // given
        val accessToken = "validAccessToken"
        val email = "test@test.com"
        val expiration = Date(System.currentTimeMillis() + 1000 * 60 * 60) // 1 hour
        val request = mockk<HttpServletRequest>()

        every { request.getHeader("Authorization") } returns "Bearer $accessToken"
        every { jwtTokenProvider.getExpiration(accessToken) } returns expiration
        every { jwtTokenProvider.getEmailFromToken(accessToken) } returns email
        every { redisService.deleteRefreshToken(email) } returns Unit
        every { redisService.saveDenylist(eq(accessToken), any()) } returns Unit

        // when
        userService.logout(request)

        // then
        verify(exactly = 1) { jwtTokenProvider.getExpiration(accessToken) }
        verify(exactly = 1) { jwtTokenProvider.getEmailFromToken(accessToken) }
        verify(exactly = 1) { redisService.deleteRefreshToken(email) }
        verify(exactly = 1) { redisService.saveDenylist(eq(accessToken), any()) }
    }
}
