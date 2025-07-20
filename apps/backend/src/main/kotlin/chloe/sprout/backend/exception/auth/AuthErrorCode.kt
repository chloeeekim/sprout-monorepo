package chloe.sprout.backend.exception.auth

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    NOT_DEFINED(HttpStatus.UNAUTHORIZED.value(), "AUTH_000", "알 수 없는 인증 오류입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "AUTH_001", "권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED.value(), "AUTH_002", "인증되지 않은 사용자입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AUTH_003", "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AUTH_004", "유효하지 않은 Refresh Token입니다."),
    MISSING_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AUTH_005", "요청에 Access Token을 찾을 수 없습니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AUTH_006", "요청에 Refresh Token을 찾을 수 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED.value(), "AUTH_007", "계정에 로그인해야 합니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}