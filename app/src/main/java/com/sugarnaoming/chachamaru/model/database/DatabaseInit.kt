package com.sugarnaoming.chachamaru.model.database

import android.database.sqlite.SQLiteDatabase

class DatabaseInit(private val db: SQLiteDatabase) {
  fun run() {
    db.run {
      val conf = ConfigDatabase()
      execSQL(conf.getSqlCreateGroupListTable)
      execSQL(conf.getSqlCreateUrlListTable)
      execSQL(conf.getSqlArticleStock)
      execSQL(conf.getSqlDefaultInsertToGroupList)
      execSQL(conf.getSqlDefaultInsertToUrlList)
    }
  }
}