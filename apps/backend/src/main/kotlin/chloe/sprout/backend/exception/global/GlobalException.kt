package chloe.sprout.backend.exception.global

import chloe.sprout.backend.exception.CustomException

class InvalidFieldValueException() : CustomException(GlobalErrorCode.INVALID_FIELD_VALUE)