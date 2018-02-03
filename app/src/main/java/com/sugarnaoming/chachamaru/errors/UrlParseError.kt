package com.sugarnaoming.chachamaru.errors

class UrlParseError: Throwable() {
  override val message: String?
    get() = ErrorMessages.urlParseError()
}