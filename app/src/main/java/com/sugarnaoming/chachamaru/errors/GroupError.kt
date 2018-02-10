package com.sugarnaoming.chachamaru.errors

class GroupError(private val errorType: Int): Throwable() {
  override val message: String?
    get() = ErrorMessages.groupError(errorType)

  companion object {
    const val NAME_DUPLICATE = 1
    const val NAME_BLANK = 2
  }
}