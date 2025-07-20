package chloe.sprout.backend.common

import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val status: Int,
    val code: String? = null,
    val message: String? = null,
    val timestamp: LocalDateTime,
    val path: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(status: Int, data: T? = null, message: String = "Success", path: String? = null): ApiResponse<T> {
            return ApiResponse(true, status, status.toString(), message, LocalDateTime.now(), path, data)
        }

        fun <T> error(status: Int, data: T? = null, message: String, code: String, path: String? = null): ApiResponse<T> {
            return ApiResponse(false, status, code, message, LocalDateTime.now(), path, data)
        }
    }
}