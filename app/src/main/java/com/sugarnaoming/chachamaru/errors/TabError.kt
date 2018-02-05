package com.sugarnaoming.chachamaru.errors

class TabError(private val errorType: Int): Throwable() {
  override val message: String?
    get() = ErrorMessages.tabError(errorType)


  companion object {
    const val NAME_OR_URL_BLANK = 1
    const val NAME_DUPLICATE = 2
  }
}