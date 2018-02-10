package com.sugarnaoming.chachamaru.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.sugarnaoming.chachamaru.R
import com.sugarnaoming.chachamaru.api.Api
import com.sugarnaoming.chachamaru.ApplicationDataHolder
import com.sugarnaoming.chachamaru.datamodel.ArticleEntity
import com.sugarnaoming.chachamaru.datamodel.ArticleConnectionEntity
import com.sugarnaoming.chachamaru.errors.Errors
import com.sugarnaoming.chachamaru.model.*
import com.sugarnaoming.chachamaru.model.adapter.ArticlesAdapter
import kotlinx.android.synthetic.main.fragment_content_article.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class FragmentContentArticle: android.support.v4.app.Fragment() {
  private lateinit var articleConnectionInfo: ArticleConnectionEntity
  private lateinit var recyclerView: RecyclerView
  private lateinit var _view: View
  private var error: Errors? = null
  private val articles: MutableList<ArticleEntity> = mutableListOf()

  // 選択されたタブが画面に表示された時にAPIを実行
  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    // Subscribeされたエラーの中で現在表示中のタブで起きた最初のエラーだけを取得する
    error = Errors(true)
    if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    EventBus.getDefault().register(this)
    this.articleConnectionInfo = arguments.getSerializable("ArticleConnectionInfo") as ArticleConnectionEntity
    ApplicationDataHolder.tabName = this.articleConnectionInfo.title
    ApplicationDataHolder.tabUrl = this.articleConnectionInfo.url
    ApplicationDataHolder.isRss = this.articleConnectionInfo.isRssUrl
    if (isVisibleToUser) {
      // URLがキャッシュされている場合はリクエストを行わない
      if (Cache.isNotExistsCachedUrl(articleConnectionInfo.url, articleConnectionInfo.title)) {
        Api().run(this.articleConnectionInfo.url, this.articleConnectionInfo.isRssUrl)
      }
    } else {
      // 表示しているタブが変化した場合にErrorsインスタンスを削除する
      error = null
    }
  }

  override fun onResume() {
    super.onResume()
    error = null
    error = Errors(true)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater!!.inflate(R.layout.fragment_content_article, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    this.recyclerView = view!!.findViewById(R.id.article_recycler_view)
    this._view = view
    val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    swipeRefresh.run {
      setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
      setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.colorPrimary))
      setOnRefreshListener {
        Api().run(articleConnectionInfo.url, articleConnectionInfo.isRssUrl)
      }
    }
    this.recyclerView.run {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(_view.context)
      adapter = ArticlesAdapter(articles, activity as Activity)
      isScrollbarFadingEnabled = true
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
    }
  }

  @Subscribe
  fun onGetArticlesSucceed(succeed: SubscribeSucceed) {
    val responseIsSucceed = succeed.response.isSucceed
    val responseBody = succeed.response.body
    val requestedUrl = succeed.response.targetUrl
    swipe_refresh.isRefreshing = false
    // 最新のリクエスト以外を破棄
    // 非同期のため連続でAPIを実行した場合に古いリクエストが配達されることがある
    if ((this.articleConnectionInfo.url != requestedUrl)) {
      EventBus.getDefault().cancelEventDelivery(this)
    }
    // リクエストしたurlをキャッシュ
    Cache.add(requestedUrl, ApplicationDataHolder.tabName)
    if (responseIsSucceed) {
      if(this.articles.size > 0) {
        this.articles.removeAll(this.articles)
        recyclerView.adapter.notifyDataSetChanged()
      }
      responseBody?.forEachIndexed { i, article ->
        this.articles.add(article)
        recyclerView.adapter.notifyItemInserted(i)
      }
    }
    this._view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
  }

  @Subscribe
  fun onFailureSubscribe(failure: SubscribeFailure) {
    this._view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
    error!!.showMessage(activity, failure.t)
  }
}