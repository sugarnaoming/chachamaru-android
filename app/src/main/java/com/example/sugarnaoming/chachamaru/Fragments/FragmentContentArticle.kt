package com.example.sugarnaoming.chachamaru.Fragments

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.example.sugarnaoming.chachamaru.ArticlesAdapter
import com.example.sugarnaoming.chachamaru.R
import com.example.sugarnaoming.chachamaru.Api.Api
import com.example.sugarnaoming.chachamaru.Datamodel.ArticleEntity
import com.example.sugarnaoming.chachamaru.Datamodel.ArticleConnectionEntity
import com.example.sugarnaoming.chachamaru.Datamodel.BadRequestException
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class FragmentContentArticle: android.support.v4.app.Fragment() {
  private lateinit var articleConnectionInfo: ArticleConnectionEntity
  private lateinit var recyclerView: RecyclerView
  private lateinit var _view: View
  private val articles: MutableList<ArticleEntity> = mutableListOf()

  // 選択されたタブが画面に表示された時にAPIを実行
  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    this.articleConnectionInfo = arguments.getSerializable("ArticleConnectionInfo") as ArticleConnectionEntity
    if (isVisibleToUser) {
      // URLがキャッシュされている場合はリクエストを行わない
      if(!this.existsCachedUrl(articleConnectionInfo.url)) {
        try {
          Api().run(this.articleConnectionInfo.url, articleConnectionInfo.isRssUrl)
          EventBus.getDefault().register(this)
        }catch (e: BadRequestException) { throw e
        }catch (e: Exception) { throw e }
      }
    } else {
      if(EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().unregister(this)
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater!!.inflate(R.layout.fragment_content_article, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    this.recyclerView = view!!.findViewById(R.id.article_recycler_view)
    this._view = view
    this.recyclerView.run {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(_view.context)
      adapter = ArticlesAdapter(articles, activity as Activity)
      isScrollbarFadingEnabled = true
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    EventBus.getDefault().unregister(this)
  }

  @Subscribe
  fun onGetArticlesSucceed(response: Api.ResponseEventClass) {
    val responseIsSucceed = response.isSucceed
    val responseBody = response.body
    val requestedUrl = response.targetUrl
    // 最新のリクエスト以外を破棄
    // 非同期のため連続でAPIを実行した場合に古いリクエストが配達されることがある
    if((this.articleConnectionInfo.url != requestedUrl)) {
      EventBus.getDefault().cancelEventDelivery(this)
    }
    // リクエストしたurlをキャッシュ
    this.addCachedUrl(requestedUrl)
    if(responseIsSucceed) {
      responseBody?.forEachIndexed { i, article ->
        this.articles.add(article)
        recyclerView.adapter.notifyItemInserted(i)
      }
    }
    this._view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
  }

  private fun addCachedUrl(url: String) {
    val cacheMax = 2
    if(cachedUrl.size == cacheMax) cachedUrl.poll()
    cachedUrl.add(url)
  }

  private fun existsCachedUrl(url: String): Boolean = cachedUrl.contains(url)

  companion object {
    val cachedUrl: Queue<String> = ArrayDeque<String>()
    fun clearCache() {
      cachedUrl.clear()}
  }
}