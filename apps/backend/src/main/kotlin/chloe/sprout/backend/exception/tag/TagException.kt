package chloe.sprout.backend.exception.tag

import chloe.sprout.backend.exception.CustomException

class TagNotFoundException() : CustomException(TagErrorCode.TAG_NOT_FOUND)
class TagNameRequiredException : CustomException(TagErrorCode.TAG_NAME_REQUIRED)
class TagOwnerMismatchException : CustomException(TagErrorCode.TAG_OWNER_MISMATCH)
class TagNameAlreadyExistsException : CustomException(TagErrorCode.TAG_NAME_ALREADY_EXISTS)