package com.sugarnaoming.chachamaru.errors

class BadRequestException(private val httpStatusCode: Int) : Throwable() {
  override val message: String?
    get() = ErrorMessages.httpError(httpStatusCode)
}