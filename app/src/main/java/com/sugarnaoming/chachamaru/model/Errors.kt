package com.sugarnaoming.chachamaru.model

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.sugarnaoming.chachamaru.R

/*
* onlyOnceがtrueの場合は同一インスタンス内の場合showAlertが一度しか実行されない
* */
class Errors(private val onlyOnce: Boolean = false) {
  init { Errors.executeCount = 0 }
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
    if(onlyOnce && Errors.executeCount != 0) return
    Errors.executeCount++
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