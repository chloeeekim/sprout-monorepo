package chloe.sprout.backend.auth

import chloe.sprout.backend.common.ApiResponse
import chloe.sprout.backend.exception.auth.AuthErrorCode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val errorCode = AuthErrorCode.ACCESS_DENIED
        val errorDetail = errorCode.getErrorDetail()

        val responseBody = ApiResponse.error<Unit>(
            status = errorDetail.status,
            message = errorDetail.message,
            code = errorDetail.code,
            path = request.requestURI
        )

        response.contentType = "application/json"
        response.status = errorDetail.status
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(responseBody))
    }
}