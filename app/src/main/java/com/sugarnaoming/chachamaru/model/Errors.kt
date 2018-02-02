package com.sugarnaoming.chachamaru.model

import android.content.Context
import android.widget.Toast

class Errors {
  fun showMessage(context: Context, t: Throwable) {
    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
  }

  fun showMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  fun showMessage(context: Context, id: Int) {
    Toast.makeText(context, context.resources.getString(id), Toast.LENGTH_SHORT).show()
  }
}