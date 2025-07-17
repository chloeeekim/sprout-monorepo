package chloe.sprout.backend.exception

open class CustomException(
    val errorCode: BaseErrorCode
) : RuntimeException()