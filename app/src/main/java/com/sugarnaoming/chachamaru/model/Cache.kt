package com.sugarnaoming.chachamaru.model

import com.sugarnaoming.chachamaru.ApplicationDataHolder
import java.util.*

object Cache {
  private val urlCache: Queue<Map<String, String>> = ArrayDeque<Map<String, String>>()
  private const val MAX_CACHE = 2
  fun clear() {
    this.urlCache.clear()
  }

  fun add(url: String, tabName: String) {
    if(urlCache.size == MAX_CACHE) urlCache.poll()
    urlCache.add(mapOf(tabName to url))
    this.normalization()
  }

  fun isExistsCachedUrl(url: String, tabName: String): Boolean = urlCache.contains(mapOf(tabName to url))
  fun isNotExistsCachedUrl(url: String, tabName: String): Boolean = !(urlCache.contains(mapOf(tabName to url)))

  private fun normalization() {
    // キャッシュ数が2以上の場合に実行される
    // キャッシュされているデータがタブの並び順と同じように連続しているかを確認
    // 連続していない場合はキャッシュすべてを消す
    /*
    * 理由：
    * ViewPagerは現在表示されている画面とその一つ前の画面をキャッシュしているっぽい（キャッシュする画面数は変更できる1以下は無理っぽい）
    * しかし、移動元と移動先の画面が（タブとして）連続していない場合に移動元の画面をキャッシュしていないらしい。
    * *この際にキャッシュされるのは移動先の1つ前の画面っぽい
    * この仕様のため移動元と移動先の画面が連続していない場合はキャッシュをクリアする処理を実装した
    * */

    //Queueに追加する段階でQueueの先頭のタイトルをタブの順番に当てはめて、Queueに追加するものを該当タブの前後と比較すればループせずに済みそう
    if (urlCache.size >= 2) {
      val dbController = DatabaseController(ApplicationDataHolder.appContext!!)
      val tabs = dbController.getUrlsByGroupNameOf(ApplicationDataHolder.groupName)
      val cacheds: MutableList<Map<String, String>> = mutableListOf()
      urlCache.forEach { cacheds.add(it) }
      var isCacheNormality = false
      // 連続性のチェック
      var count = 0
      tabs.forEach {
        if (cacheds[0].containsKey(it.title) && cacheds[0].containsValue(it.url)) { count++
        } else if (cacheds[1].containsKey(it.title) && cacheds[1].containsValue(it.url)) { count++
        } else { count = 0 }
        if (count == 2) {
          isCacheNormality = true
          return@forEach
        }
      }
      if (!isCacheNormality) this.clear()
    }
  }
}