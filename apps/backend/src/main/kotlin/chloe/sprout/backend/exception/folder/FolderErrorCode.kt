package chloe.sprout.backend.exception.folder

import chloe.sprout.backend.exception.BaseErrorCode
import chloe.sprout.backend.exception.ErrorDetail
import org.springframework.http.HttpStatus

enum class FolderErrorCode(
    val status: Int,
    val code: String,
    val message: String
) : BaseErrorCode {
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "FOLDER_001", "폴더를 찾을 수 없습니다."),
    FOLDER_NAME_REQUIRED(HttpStatus.BAD_REQUEST.value(), "FOLDER_002", "폴더 이름은 필수입니다."),
    FOLDER_OWNER_MISMATCH(HttpStatus.FORBIDDEN.value(), "FOLDER_003", "해당 폴더에 접근할 권한이 없습니다.");

    override fun getErrorDetail(): ErrorDetail {
        return ErrorDetail(status, code, message)
    }
}