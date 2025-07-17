package chloe.sprout.backend.service

import chloe.sprout.backend.auth.JwtTokenProvider
import chloe.sprout.backend.domain.Refresh
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.UserLoginRequest
import chloe.sprout.backend.dto.UserLoginResponse
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.exception.user.InvalidPasswordException
import chloe.sprout.backend.exception.user.UserAlreadyExistsException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.RefreshRepository
import chloe.sprout.backend.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshRepository: RefreshRepository
) {
    @Transactional
    fun signup(request: UserSignupRequest): UserSignupResponse {
        // email 중복 확인
        userRepository.findByEmail(request.email)?.let {
            throw UserAlreadyExistsException()
        }

        // password 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

        // User entity 생성
        val user = User(
            email = request.email,
            password = encodedPassword,
            name = request.name
        )

        // DB 저장
        val savedUser = userRepository.save(user)

        // response DTO로 변환 후 반환
        return UserSignupResponse.from(savedUser)
    }

    @Transactional(readOnly = true)
    fun login(request: UserLoginRequest, httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): UserLoginResponse {
        // 사용자 확인
        val user = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException()

        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidPasswordException()
        }

        // JWT access token 발급 후 header에 추가
        val accessToken = jwtTokenProvider.generateAccessToken(user.email)
        httpResponse.setHeader("Authorization", "Bearer $accessToken")

        // JWT refresh token 발급 후 cookie에 추가
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.email)
        val refresh = Refresh(email = user.email, refreshToken = refreshToken)
        refreshRepository.save(refresh)

        val cookie = ResponseCookie.from("refreshToken", refreshToken).apply {
            httpOnly(true)
            secure(true)
            path("/")
            maxAge(Duration.ofDays(14))
            sameSite("Lax")
        }.build()

        httpResponse.addHeader("Set-Cookie", cookie.toString())

        // response DTO로 변환 후 반환
        return UserLoginResponse.from(user)
    }
}