package com.sugarnaoming.chachamaru.model.database

class ConfigDatabase {
  val getSqlCreateGroupListTable: String
    get() = "create table ${TABLE_NAME_GROUPLIST} (" +
        "_id integer primary key autoincrement, " +
        "group_name text not null unique)"
  val getSqlCreateUrlListTable: String
    get() = "create table ${TABLE_NAME_URLLIST} (" +
        "_id integer primary key autoincrement," +
        "title text not null," +
        "group_name text not null," +
        "url text not null," +
        "is_rss boolean not null," +
        "unique(title, group_name)," +
        "foreign key(group_name) references ${TABLE_NAME_GROUPLIST}(group_name)" +
        "on update cascade on delete cascade)"
  val getSqlArticleStock: String
    get() = "create table ${TABLE_NAME_STOCK} (" +
        "_id integer primary key autoincrement," +
        "group_name text not null," +
        "title text not null," +
        "description text," +
        "url text not null," +
        "unique(title, group_name))"
  val getSqlDefaultInsertToGroupList: String
    get() = "insert into ${TABLE_NAME_GROUPLIST} (group_name)" +
        "values('はてなブックマーク/新着')," +
        "('はてなブックマーク/人気')," +
        "('Qiita')," +
        "('Web')," +
        "('情報全般')"
  val getSqlDefaultInsertToUrlList: String
    get() {
      val hatebuEntryBaseUrl = "http://b.hatena.ne.jp/entrylist"
      val hatebuHotBaseUrl = "http://b.hatena.ne.jp/hotentry"
      val qiitaBaseUrl = "http://sugarnaoming.com/api/qiita/rank"
      val suffix = ".rss"
      val rssTrue = 1
      val rssFalse = 0
      val categoriesName = listOf("", "/social", "/economics", "/life", "/knowledge", "/it", "/entertainment", "/game", "/fun")
      val categories = categoriesName.map { "$it$suffix" }
      return "insert into ${TABLE_NAME_URLLIST}(title, group_name, url, is_rss) " +
          "values('総合', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[0]}', $rssTrue)," +
          "('テクノロジー', 'はてなブックマーク/新着', '$hatebuEntryBaseUrl${categories[5]}', $rssTrue)," +
          "('総合', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[0]}', $rssTrue)," +
          "('テクノロジー', 'はてなブックマーク/人気', '$hatebuHotBaseUrl${categories[5]}', $rssTrue)," +
          "('デイリーランキング', 'Qiita', '$qiitaBaseUrl/daily', $rssFalse)," +
          "('ウィークリーランキング', 'Qiita', '$qiitaBaseUrl/weekly', $rssFalse)," +
          "('人気ランキング', 'Qiita', '$qiitaBaseUrl/popular', $rssFalse)," +
          "('コリス', 'Web', 'http://coliss.com/feed/', $rssTrue)," +
          "('GIGAZINE', '情報全般', 'http://feed.rssad.jp/rss/gigazine/rss_atom', $rssTrue)"
    }
  companion object {
    val TABLE_NAME_GROUPLIST = "grouplist"
    val TABLE_NAME_URLLIST = "urllist"
    val TABLE_NAME_STOCK = "stock"
    val INVOKE_FOREINKEY_SQL = "PRAGMA foreign_keys = ON"
  }
}