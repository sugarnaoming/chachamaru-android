package com.sugarnaoming.chachamaru

import android.database.sqlite.SQLiteDatabase

class DatabaseInit(private val db: SQLiteDatabase) {
  fun run() {
    db.run {
      val conf = ConfigDatabase()
      execSQL(conf.invokeForeignKeySql)
      execSQL(conf.getSqlCreateGroupListTable)
      execSQL(conf.getSqlCreateUrlListTable)
      execSQL(conf.getSqlDefaultInsertToGroupList)
      execSQL(conf.getSqlDefaultInsertToUrlList)
    }
  }
}