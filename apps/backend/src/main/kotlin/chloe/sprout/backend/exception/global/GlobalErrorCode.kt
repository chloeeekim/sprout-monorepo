package chloe.sprout.backend.exception.global

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    INVALID_FIELD_VALUE(HttpStatus.BAD_REQUEST.value(), "GLOBAL_001", "잘못된 필드 값입니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}