package com.sugarnaoming.chachamaru.Datamodel

class BadRequestException(val httpStatusCode: Int): Throwable() {
    override val message: String?
        get() {
            when (httpStatusCode) {
                404 -> return ErrorMessages.httpStatus_404
                else -> return ErrorMessages.httpStatus_other
            }
        }
}