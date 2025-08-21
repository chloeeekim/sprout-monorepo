package chloe.sprout.backend.exception.tag

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class TagErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "TAG_001", "태그를 찾을 수 없습니다."),
    TAG_NAME_REQUIRED(HttpStatus.BAD_REQUEST.value(), "TAG_002", "태그 이름은 필수입니다."),
    TAG_OWNER_MISMATCH(HttpStatus.FORBIDDEN.value(), "TAG_003", "해당 태그에 접근할 권한이 없습니다."),
    TAG_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "TAG_004", "이미 존재하는 태그 이름입니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}