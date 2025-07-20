package chloe.sprout.backend.exception.auth

import org.springframework.security.core.AuthenticationException

abstract class BaseAuthException(
    val errorCode: AuthErrorCode
) : AuthenticationException(errorCode.message)