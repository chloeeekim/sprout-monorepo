package chloe.sprout.backend.auth

import chloe.sprout.backend.common.ApiResponse
import chloe.sprout.backend.exception.auth.AuthErrorCode
import chloe.sprout.backend.exception.auth.InvalidAccessTokenException
import chloe.sprout.backend.exception.auth.LoginRequiredException
import chloe.sprout.backend.exception.auth.MissingAccessTokenException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val errorCode = when (authException) {
            is LoginRequiredException -> authException.errorCode
            is InvalidAccessTokenException -> authException.errorCode
            is MissingAccessTokenException -> authException.errorCode
            else -> AuthErrorCode.NOT_DEFINED
        }

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