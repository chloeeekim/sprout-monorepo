package chloe.sprout.backend.service

import chloe.sprout.backend.domain.User
import chloe.sprout.backend.dto.UserSignupRequest
import chloe.sprout.backend.dto.UserSignupResponse
import chloe.sprout.backend.exception.UserAlreadyExistsException
import chloe.sprout.backend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun signup(request: UserSignupRequest): UserSignupResponse {
        // email 중복 확인
        userRepository.findByEmail(request.email)
            ?: throw UserAlreadyExistsException("이미 존재하는 이메일입니다.")

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
}