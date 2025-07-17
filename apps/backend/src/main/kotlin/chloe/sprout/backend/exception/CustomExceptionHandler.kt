package chloe.sprout.backend.exception

import chloe.sprout.backend.common.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        return buildResponse(e.errorCode.getErrorDetail(), httpRequest)
    }

    private fun buildResponse(errorDetail: ErrorDetail, httpRequest: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(errorDetail.status).body(
            ApiResponse.error(
                status = errorDetail.status,
                message = errorDetail.message,
                code = errorDetail.code,
                path = httpRequest.requestURI
            )
        )
    }
}