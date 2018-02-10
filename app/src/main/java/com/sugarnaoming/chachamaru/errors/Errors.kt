package com.sugarnaoming.chachamaru.errors

import android.app.AlertDialog
import android.content.Context
import com.sugarnaoming.chachamaru.R

/*
* onlyOnceがtrueの場合は同一インスタンス内の場合showAlertが一度しか実行されない
* */
class Errors(private val onlyOnce: Boolean = false) {
  init { executeCount = 0 }
  fun showMessage(context: Context, t: Throwable) {
    showAlert(context, t.message.toString())
  }

  fun showMessage(context: Context, message: String) {
    showAlert(context, message)
  }

  fun showMessage(context: Context, id: Int) {
    showAlert(context, context.resources.getString(id))
  }

  private fun showAlert(context: Context, message: String) {
    if(onlyOnce && executeCount != 0) return
    executeCount++
    val alert = AlertDialog.Builder(context)
    alert.run {
      setTitle("エラー")
      setMessage(message)
      setPositiveButton(R.string.ok__button, null)
      show()
    }
  }
  companion object {
    var executeCount = 0
  }
}