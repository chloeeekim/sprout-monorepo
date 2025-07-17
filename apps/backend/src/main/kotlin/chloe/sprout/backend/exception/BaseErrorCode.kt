package chloe.sprout.backend.exception

interface BaseErrorCode{
    fun getErrorDetail(): ErrorDetail
}