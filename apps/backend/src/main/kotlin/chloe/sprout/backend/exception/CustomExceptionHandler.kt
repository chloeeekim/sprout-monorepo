package chloe.sprout.backend.exception

import chloe.sprout.backend.common.ApiResponse
import chloe.sprout.backend.exception.global.GlobalErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        val errorDetail = e.errorCode.getErrorDetail()

        return ResponseEntity.status(errorDetail.status).body(
            ApiResponse.error(
                status = errorDetail.status,
                message = errorDetail.message,
                code = errorDetail.code,
                path = httpRequest.requestURI
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<List<String>>> {
        val validationErrors = e.bindingResult
            .fieldErrors.mapNotNull { it.defaultMessage }

        val errorDetail = GlobalErrorCode.INVALID_FIELD_VALUE.getErrorDetail()

        return ResponseEntity.status(errorDetail.status).body(
            ApiResponse.error(
                status = errorDetail.status,
                message = errorDetail.message,
                code = errorDetail.code,
                path = httpRequest.requestURI,
                data = validationErrors
            )
        )
    }
}