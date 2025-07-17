package chloe.sprout.backend.service

import chloe.sprout.backend.auth.JwtTokenProvider
import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.UserLoginRequest
import chloe.sprout.backend.dto.UserLoginResponse
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.exception.user.InvalidPasswordException
import chloe.sprout.backend.exception.user.UserAlreadyExistsException
import chloe.sprout.backend.exception.user.UserNotFoundException
import chloe.sprout.backend.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
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

        // JWT token 발급 후 header에 추가
        val token = jwtTokenProvider.generateToken(user.email)
        httpResponse.setHeader("Authorization", "Bearer $token")

        // response DTO로 변환 후 반환
        return UserLoginResponse.from(user)
    }
}