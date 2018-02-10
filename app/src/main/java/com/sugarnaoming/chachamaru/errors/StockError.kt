package com.sugarnaoming.chachamaru.errors

class StockError: Throwable() {
  override val message: String?
    get() = ErrorMessages.StockError()
}