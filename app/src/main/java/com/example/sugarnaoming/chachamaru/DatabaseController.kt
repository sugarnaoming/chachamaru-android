package com.example.sugarnaoming.chachamaru

import android.content.Context
import com.example.sugarnaoming.chachamaru.Datamodel.ArticleConnectionEntity

class DatabaseController(applicationContext: Context) {
  private val dbHelper = GetUrlDatabaseHelper(applicationContext)

  fun getAll(): List<ArticleConnectionEntity> {
    val sql = "select * from urllist"
    val list: MutableList<ArticleConnectionEntity> = mutableListOf()
    val db = dbHelper.readableDatabase
    db.rawQuery(sql, null).use {
      while (it.moveToNext()) {
          list.add(ArticleConnectionEntity(
              it.getInt(0),
              it.getString(1),
              it.getString(2),
              it.getString(3),
              this.intToBool(it.getInt(4))
          ))
        }
      }
    db.close()
    return list
  }

  fun getUrlsByGroupNameOf(groupName: String): List<ArticleConnectionEntity> {
    val sql = "select * from urllist where group_name = ?"
    val list: MutableList<ArticleConnectionEntity> = mutableListOf()
    val db = dbHelper.readableDatabase
    db.rawQuery(sql, arrayOf(groupName)).use {
      while (it.moveToNext()) {
        list.add(ArticleConnectionEntity(
            it.getInt(0),
            it.getString(1),
            it.getString(2),
            it.getString(3),
            this.intToBool(it.getInt(4))
        ))
      }
    }
    db.close()
    return list
  }

  fun getGroupNamesWhereUrlExists(): Set<String> {
    val sql = "select distinct group_name from urllist"
    val set: MutableSet<String> = mutableSetOf()
    val db = dbHelper.readableDatabase
    db.rawQuery(sql, null).use {
      while (it.moveToNext()) {
        set.add(it.getString(0))
      }
    }
    db.close()
    return set
  }

  private fun intToBool(int: Int): Boolean {
    if(int == 0) return false
    return true
  }

  private fun boolToInt(bool: Boolean): Int {
    if(bool) return 1
    return 0
  }
}
