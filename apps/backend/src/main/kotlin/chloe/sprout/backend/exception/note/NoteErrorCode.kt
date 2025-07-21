package chloe.sprout.backend.exception.note

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class NoteErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NOTE_001", "노트를 찾을 수 없습니다."),
    TITLE_TITLE_REQUIRED(HttpStatus.BAD_REQUEST.value(), "NOTE_002", "노트 제목은 필수입니다."),
    NOTE_OWNER_MISMATCH(HttpStatus.FORBIDDEN.value(), "NOTE_003", "해당 노트에 접근할 권한이 없습니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}