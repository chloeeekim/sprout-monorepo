package chloe.sprout.backend.exception

class UserAlreadyExistsException(message: String): RuntimeException(message)
class UserNotFoundException(message: String): RuntimeException(message)
class InvalidPasswordException(message: String): RuntimeException(message)