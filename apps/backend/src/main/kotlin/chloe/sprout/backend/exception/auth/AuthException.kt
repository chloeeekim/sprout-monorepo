package chloe.sprout.backend.exception.auth

class ForbiddenException() : BaseAuthException(AuthErrorCode.FORBIDDEN)
class AccessDeniedException() : BaseAuthException(AuthErrorCode.ACCESS_DENIED)
class InvalidAccessTokenException() : BaseAuthException(AuthErrorCode.INVALID_ACCESS_TOKEN)
class InvalidRefreshTokenException() : BaseAuthException(AuthErrorCode.INVALID_REFRESH_TOKEN)
class MissingAccessTokenException() : BaseAuthException(AuthErrorCode.MISSING_ACCESS_TOKEN)
class MissingRefreshTokenException() : BaseAuthException(AuthErrorCode.MISSING_REFRESH_TOKEN)
class LoginRequiredException() : BaseAuthException(AuthErrorCode.LOGIN_REQUIRED)
