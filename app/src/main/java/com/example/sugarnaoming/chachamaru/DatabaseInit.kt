package com.example.sugarnaoming.chachamaru

import android.database.sqlite.SQLiteDatabase

class DatabaseInit(private val db: SQLiteDatabase) {
  fun run() {
    db.run {
      execSQL(invokeForeignKeySql())
      execSQL(createGroupListTableSql())
      execSQL(createUrlListTableSql())
      execSQL(insertGroupListSql())
      execSQL(insertUrlListSql())
    }
  }

  private fun invokeForeignKeySql(): String = "PRAGMA foreign_keys = ON"
  private fun createGroupListTableSql(): String = "create table grouplist(" +
        "_id integer primary key autoincrement," +
        "group_name text not null unique)"
  private fun createUrlListTableSql(): String = "create table urllist(" +
        "_id integer primary key autoincrement," +
        "title text not null," +
        "group_name text not null," +
        "url text not null," +
        "is_rss boolean not null," +
        "unique(title, group_name)," +
        "foreign key(group_name) references grouplist(group_name))"
  private fun insertGroupListSql(): String = "insert into grouplist(group_name)" +
        "values('はてなブックマーク/新着')," +
        "('はてなブックマーク/人気')," +
        "('Qiita')," +
        "('Web')," +
        "('情報全般')"
  private fun insertUrlListSql(): String {
    val hatebuEntryBaseUrl = "http://b.hatena.ne.jp/entrylist"
    val hatebuHotBaseUrl = "http://b.hatena.ne.jp/hotentry"
    val qiitaBaseUrl = "api/qiita/rank"
    val suffix = ".rss"
    val rssTrue = 1
    val rssFalse = 0
    val categoriesName = listOf("", "/social", "/economics", "/life", "/knowledge", "/it", "/entertainment", "/game", "/fun")
    val categories = categoriesName.map { "$it$suffix" }
    return "insert into urllist(title, group_name, url, is_rss) " +
        "values('総合', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[0]}', $rssTrue)," +
        "('世の中', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[1]}', $rssTrue)," +
        "('政治と経済', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[2]}', $rssTrue)," +
        "('暮らし', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[3]}', $rssTrue)," +
        "('学び', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[4]}', $rssTrue)," +
        "('テクノロジー', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[5]}', $rssTrue)," +
        "('エンタメ', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[6]}', $rssTrue)," +
        "('アニメとゲーム', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[7]}', $rssTrue)," +
        "('おもしろ', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[8]}', $rssTrue)," +
        "('総合', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[0]}', $rssTrue)," +
        "('世の中', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[1]}', $rssTrue)," +
        "('政治と経済', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[2]}', $rssTrue)," +
        "('暮らし', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[3]}', $rssTrue)," +
        "('学び', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[4]}', $rssTrue)," +
        "('テクノロジー', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[5]}', $rssTrue)," +
        "('エンタメ', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[6]}', $rssTrue)," +
        "('アニメとゲーム', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[7]}', $rssTrue)," +
        "('おもしろ', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[8]}', $rssTrue)," +
        "('デイリーランキング', 'Qiita', '$qiitaBaseUrl/daily', $rssFalse)," +
        "('ウィークリーランキング', 'Qiita', '$qiitaBaseUrl/weekly', $rssFalse)," +
        "('人気ランキング', 'Qiita', '$qiitaBaseUrl/popular', $rssFalse)," +
        "('コリス', 'Web', 'http://coliss.com/feed/', $rssTrue)," +
        "('GIGAZINE', '情報全般', 'http://feed.rssad.jp/rss/gigazine/rss_atom', $rssTrue)"
  }
}