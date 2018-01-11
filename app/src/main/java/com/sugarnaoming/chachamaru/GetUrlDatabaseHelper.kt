package com.sugarnaoming.chachamaru

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GetUrlDatabaseHelper(applicationContext: Context): SQLiteOpenHelper(applicationContext, "GetUrlDatabase", null, 1) {
  override fun onCreate(db: SQLiteDatabase?) {
    DatabaseInit(db!!).run()
  }

  override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
  }
}