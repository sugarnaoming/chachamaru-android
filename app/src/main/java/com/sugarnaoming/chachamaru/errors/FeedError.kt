package com.sugarnaoming.chachamaru.errors

class FeedError(private val errorType: Int): Throwable() {
  override val message: String?
    get() = ErrorMessages.feedError(errorType)

  companion object {
    const val PARSE_ERROR = 1
    const val FILE_NOT_FOUND = 2
    const val PROBLEM_READ_FILE = 3
    const val FEED_TYPE_NOT_UNDERSTAND = 4
    const val UNEXPECTED = 100
  }
}