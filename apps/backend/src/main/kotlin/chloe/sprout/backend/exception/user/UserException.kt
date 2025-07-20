package chloe.sprout.backend.exception.user

import chloe.sprout.backend.exception.CustomException

class UserNotFoundException() : CustomException(UserErrorCode.USER_NOT_FOUND)
class InvalidPasswordException() : CustomException(UserErrorCode.INVALID_PASSWORD)
class UserAlreadyExistsException() : CustomException(UserErrorCode.USER_ALREADY_EXISTS)
