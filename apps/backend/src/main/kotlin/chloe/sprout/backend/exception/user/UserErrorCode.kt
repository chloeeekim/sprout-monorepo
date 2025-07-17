package chloe.sprout.backend.exception.user

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "USER_001", "권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED.value(), "USER_002", "인증되지 않은 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USER_003", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "USER_004", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "USER_005", "이미 존재하는 이메일입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "USER_006", "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "USER_007", "유효하지 않은 Refresh Token입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "USER_008", "요청에 Refresh Token을 찾을 수 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED.value(), "USER_009", "계정에 로그인해야 합니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}