package com.sugarnaoming.chachamaru.model.database

import android.content.Context
import com.sugarnaoming.chachamaru.datamodel.ArticleConnectionEntity
import com.sugarnaoming.chachamaru.datamodel.ArticleStock

class DatabaseController(applicationContext: Context) {
  private val dbHelper = GetUrlDatabaseHelper(applicationContext)

  fun updateGroup(newGroupName: String, oldGroupName: String) {
    val sql = "update ${ConfigDatabase.TABLE_NAME_GROUPLIST} set group_name = ? where group_name = ?"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(newGroupName, oldGroupName))
  }

  fun updateUrl(newTabName: String, newTabUrl: String, isRss: Boolean, oldTabName: String, groupName: String) {
    val sql = "update ${ConfigDatabase.TABLE_NAME_URLLIST} set title = ?, url = ?, is_rss = ? where title = ? and group_name = ?"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(newTabName, newTabUrl, boolToInt(isRss), oldTabName, groupName))
  }

  fun deleteUrl(tabName: String, tabUrl: String, groupName: String) {
    val sql = "delete from ${ConfigDatabase.TABLE_NAME_URLLIST} where title = ? and url = ? and group_name = ?"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(tabName, tabUrl, groupName))
  }

  fun deleteGroup(groupName: String) {
    val sql = "delete from ${ConfigDatabase.TABLE_NAME_GROUPLIST} where group_name = ?"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(groupName))
  }

  fun addUrl(groupName: String, tabName: String, tabUrl: String, isRss: Boolean) {
    val sql = "insert into ${ConfigDatabase.TABLE_NAME_URLLIST}(title, group_name, url, is_rss)" +
        "values(?, ?, ?, ?)"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(tabName, groupName, tabUrl, boolToInt(isRss)))
  }

  fun addGroup(groupName: String) {
    val sql = "insert into ${ConfigDatabase.TABLE_NAME_GROUPLIST}(group_name) values(?)"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(groupName))
  }

  fun getAllGroupList(): List<String> {
    val sql = "select * from ${ConfigDatabase.TABLE_NAME_GROUPLIST}"
    val list: MutableList<String> = mutableListOf()
    val db = dbHelper.readableDatabase
    db.rawQuery(sql, null).use {
      while (it.moveToNext()) {
        list.add(it.getString(1))
      }
    }
    return list
  }

  fun getAllStock(): List<ArticleStock> {
    val sql = "select * from ${ConfigDatabase.TABLE_NAME_STOCK}"
    val list: MutableList<ArticleStock> = mutableListOf()
    val db = dbHelper.readableDatabase
    db.rawQuery(sql, null).use {
      while(it.moveToNext()) {
        list.add(ArticleStock(
            groupName = it.getString(1),
            title = it.getString(2),
            description = it.getString(3),
            url = it.getString(4)))
      }
    }
    return list
  }

  fun isNotDuplicateArticleInStock(article: ArticleStock): Boolean {
    val sql = "select count(*) from ${ConfigDatabase.TABLE_NAME_STOCK} where group_name = ? and title = ?"
    val db = dbHelper.readableDatabase
    var count = 0
    db.rawQuery(sql, arrayOf(article.groupName, article.title)).use {
      it.moveToNext()
      count = it.getInt(0)
    }
    return count == 0
  }

  fun addStock(article: ArticleStock) {
    val sql = "insert into ${ConfigDatabase.TABLE_NAME_STOCK}(group_name, title, description, url)" +
        "values(?, ?, ?, ?)"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(article.groupName, article.title, article.description, article.url))
  }

  fun deleteStock(article: ArticleStock) {
    val sql = "delete from ${ConfigDatabase.TABLE_NAME_STOCK} where group_name = ? and title = ?"
    val db = dbHelper.writableDatabase
    db.execSQL(sql, arrayOf(article.groupName, article.title))
  }

  fun howManyTabNamesAreInGroup(groupName: String, tabName: String): Int{
    val sql = "select count(*) from ${ConfigDatabase.TABLE_NAME_URLLIST} where group_name = ? and title = ?"
    val db = dbHelper.readableDatabase
    var count = 0
    db.rawQuery(sql, arrayOf(groupName, tabName)).use {
      while(it.moveToNext()) {
        count = it.getInt(0)
      }
    }
    return count
  }

  fun getAllUrlList(): List<ArticleConnectionEntity> {
    val sql = "select * from ${ConfigDatabase.TABLE_NAME_URLLIST}"
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
    val sql = "select * from ${ConfigDatabase.TABLE_NAME_URLLIST} where group_name = ?"
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
    val sql = "select distinct group_name from ${ConfigDatabase.TABLE_NAME_URLLIST}"
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
