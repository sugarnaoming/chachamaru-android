package com.sugarnaoming.chachamaru.errors

class UnexpectedError: Throwable() {
  override val message: String?
    get() = ErrorMessages.unexpectedError()
}