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
        "('総合')," +
        "('ガジェット')," +
        "('Develop')"
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
          "('GIGAZINE', '総合', 'http://feed.rssad.jp/rss/gigazine/rss_atom', $rssTrue)," +
          "('GIZMODO', '総合', 'https://www.gizmodo.jp/index.xml', $rssTrue)," +
          "('PhotoshopVIP', 'Web', 'http://photoshopvip.net/feed', $rssTrue)," +
          "('Build Insider', 'Develop', 'https://www.buildinsider.net/rss', $rssTrue)," +
          "('CodeZine', 'Develop', 'https://codezine.jp/rss/new/20/index.xml', $rssTrue)," +
          "('@IT', 'Develop', 'http://rss.rssad.jp/rss/itmatmarkit/rss.xml', $rssTrue)," +
          "('TechCrunch Japan', '総合', 'http://jp.techcrunch.com/feed/', $rssTrue)," +
          "('TechWave', '総合', 'http://techwave.jp/feed', $rssTrue)," +
          "('creive', 'Web', 'https://creive.me/feed/', $rssTrue)," +
          "('ガジェット通信', 'ガジェット', 'http://getnews.jp/feed/ext/orig', $rssTrue)," +
          "('CNET Japan', '総合', 'http://feeds.japan.cnet.com/rss/cnet/all.rdf', $rssTrue)," +
          "('ZDNet Japan', '総合', 'http://feeds.japan.zdnet.com/rss/zdnet/all.rdf', $rssTrue)," +
          "('Engadget Japanes', 'ガジェット', 'http://japanese.engadget.com/rss.xml', $rssTrue)," +
          "('GGSOKU', 'ガジェット', 'https://ggsoku.com/feed/', $rssTrue)"
    }
  companion object {
    val TABLE_NAME_GROUPLIST = "grouplist"
    val TABLE_NAME_URLLIST = "urllist"
    val TABLE_NAME_STOCK = "stock"
    val INVOKE_FOREINKEY_SQL = "PRAGMA foreign_keys = ON"
    val DB_VERSION = 1
    val DB_NAME = "ChaChaMaruDatabase"
  }
}