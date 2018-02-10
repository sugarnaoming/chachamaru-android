package com.sugarnaoming.chachamaru.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GetUrlDatabaseHelper(applicationContext: Context): SQLiteOpenHelper(applicationContext, "GetUrlDatabase", null, 1) {
  override fun onCreate(db: SQLiteDatabase?) {
    DatabaseInit(db!!).run()
  }

  override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
  }

  override fun onOpen(db: SQLiteDatabase?) {
    super.onOpen(db)
    //if(db!!.isReadOnly)
    db!!.execSQL(ConfigDatabase.INVOKE_FOREINKEY_SQL)
  }
}