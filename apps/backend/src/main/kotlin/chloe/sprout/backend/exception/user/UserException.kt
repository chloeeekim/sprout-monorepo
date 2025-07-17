package chloe.sprout.backend.exception.user

import chloe.sprout.backend.exception.CustomException

class ForbiddenException() : CustomException(UserErrorCode.FORBIDDEN)
class AccessDeniedException() : CustomException(UserErrorCode.ACCESS_DENIED)
class UserNotFoundException() : CustomException(UserErrorCode.USER_NOT_FOUND)
class InvalidPasswordException() : CustomException(UserErrorCode.INVALID_PASSWORD)
class UserAlreadyExistsException() : CustomException(UserErrorCode.USER_ALREADY_EXISTS)
class InvalidAccessTokenException() : CustomException(UserErrorCode.INVALID_ACCESS_TOKEN)
class InvalidRefreshTokenException() : CustomException(UserErrorCode.INVALID_REFRESH_TOKEN)
class MissingRefreshTokenException() : CustomException(UserErrorCode.MISSING_REFRESH_TOKEN)
class LoginRequiredException() : CustomException(UserErrorCode.LOGIN_REQUIRED)
