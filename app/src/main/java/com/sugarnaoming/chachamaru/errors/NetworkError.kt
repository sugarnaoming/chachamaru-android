package com.sugarnaoming.chachamaru.errors

class NetworkError(private val t: Throwable): Throwable() {
  override val message: String?
    get() = ErrorMessages.networkError(t)
}