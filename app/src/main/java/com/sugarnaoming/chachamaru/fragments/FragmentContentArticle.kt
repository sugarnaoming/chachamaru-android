package com.sugarnaoming.chachamaru.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.sugarnaoming.chachamaru.R
import com.sugarnaoming.chachamaru.api.Api
import com.sugarnaoming.chachamaru.ApplicationDataHolder
import com.sugarnaoming.chachamaru.datamodel.ArticleEntity
import com.sugarnaoming.chachamaru.datamodel.ArticleConnectionEntity
import com.sugarnaoming.chachamaru.datamodel.BadRequestException
import com.sugarnaoming.chachamaru.model.*
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
    ApplicationDataHolder.tabName = this.articleConnectionInfo.title
    ApplicationDataHolder.tabUrl = this.articleConnectionInfo.url
    ApplicationDataHolder.isRss = this.articleConnectionInfo.isRssUrl
    if (isVisibleToUser) {
      // URLがキャッシュされている場合はリクエストを行わない
      if (Cache.isNotExistsCachedUrl(articleConnectionInfo.url, articleConnectionInfo.title)) {
        try {
          Api().run(this.articleConnectionInfo.url, this.articleConnectionInfo.isRssUrl)
        } catch (e: BadRequestException) {
          Errors().showMessage(ApplicationDataHolder.appContext!!, e)
        } catch (e: Exception) {
          Errors().showMessage(ApplicationDataHolder.appContext!!, e)
        }
        EventBus.getDefault().register(this)
      }
    } else {
      if (EventBus.getDefault().isRegistered(this)) {
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
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
    }
  }

  @Subscribe
  fun onGetArticlesSucceed(succeed: SubscribeSucceed) {
    val responseIsSucceed = succeed.response.isSucceed
    val responseBody = succeed.response.body
    val requestedUrl = succeed.response.targetUrl
    // 最新のリクエスト以外を破棄
    // 非同期のため連続でAPIを実行した場合に古いリクエストが配達されることがある
    if ((this.articleConnectionInfo.url != requestedUrl)) {
      EventBus.getDefault().cancelEventDelivery(this)
    }
    // リクエストしたurlをキャッシュ
    Cache.add(requestedUrl, ApplicationDataHolder.tabName)
    if (responseIsSucceed) {
      responseBody?.forEachIndexed { i, article ->
        this.articles.add(article)
        recyclerView.adapter.notifyItemInserted(i)
      }
    }
    this._view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
  }

  @Subscribe
  fun onGetArticlesFailure(failure: SubscribeFailure) {
    this._view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
    Errors().showMessage(ApplicationDataHolder.appContext!!, failure.t)
  }
}