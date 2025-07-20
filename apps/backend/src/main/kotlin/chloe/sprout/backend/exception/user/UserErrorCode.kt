package chloe.sprout.backend.exception.user

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USER_001", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "USER_002", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "USER_003", "이미 존재하는 이메일입니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}