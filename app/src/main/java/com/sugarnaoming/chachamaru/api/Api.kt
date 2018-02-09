package com.sugarnaoming.chachamaru.api

import com.rometools.rome.io.FeedException
import com.rometools.rome.io.SyndFeedInput
import com.sugarnaoming.chachamaru.datamodel.*
import com.sugarnaoming.chachamaru.errors.*
import com.sugarnaoming.chachamaru.model.SubscribeFailure
import com.sugarnaoming.chachamaru.model.SubscribeSucceed
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.io.StringReader
import java.net.MalformedURLException
import java.net.URL

class Api {
  fun run(targetUrl: String, isRss: Boolean) {
    try {
      val url = URL(targetUrl)
      val baseUrl = this.extractBaseUrl(url)
      val urlPath = this.extractUrlPath(url)
      val urlHolder = UrlParsedHolder(fullUrl = targetUrl, baseUrl = baseUrl, urlPath = urlPath)
      if (isRss) receiveRss(urlHolder) else receiveApi(urlHolder)
    }catch (e: MalformedURLException) {
      EventBus.getDefault().post(SubscribeFailure(UrlParseError()))
    }catch (e: Exception) {
      EventBus.getDefault().post(SubscribeFailure(UnexpectedError()))
    }
  }

  private fun receiveApi(urlHolder: UrlParsedHolder) {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlHolder.baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: ApiUrl = retrofit.create(ApiUrl::class.java)
    service.getArticles(urlHolder.urlPath).enqueue(object : Callback<List<ArticleEntity>> {
      override fun onFailure(call: Call<List<ArticleEntity>>?, t: Throwable?) {
        executeFailure(t!!)
      }

      override fun onResponse(call: Call<List<ArticleEntity>>?, response: Response<List<ArticleEntity>>?) {
        if(!response!!.isSuccessful) throw BadRequestException(response.code())
        val succeed = SubscribeSucceed(ResponseEventClass(response.isSuccessful, response.body()!!, urlHolder.fullUrl, response.code()))
        EventBus.getDefault().post(succeed)
      }
    })
  }

  private fun receiveRss(urlHolder: UrlParsedHolder) {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlHolder.baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    val service: RssUrl = retrofit.create(RssUrl::class.java)
    service.getArticles(urlHolder.urlPath).enqueue(object: Callback<String> {
      override fun onFailure(call: Call<String>?, t: Throwable?) {
        executeFailure(t!!)
      }

      override fun onResponse(call: Call<String>?, response: Response<String>?) {
        if(!response!!.isSuccessful) {
          val failure = SubscribeFailure(BadRequestException(response.code()))
          EventBus.getDefault().post(failure)
        } else {
          try {
            val feed = SyndFeedInput().build(StringReader(response.body()))
            val articles = feed.entries.map {
              val description = if (it.description == null) it.contents.first().value else it.description.value
              ArticleEntity(it.title, it.link, description)
            }
            val succeed = SubscribeSucceed(ResponseEventClass(response.isSuccessful, articles, urlHolder.fullUrl, response.code()))
            EventBus.getDefault().post(succeed)
          } catch (e: FeedException) {
            EventBus.getDefault().post(SubscribeFailure(FeedError(FeedError.PARSE_ERROR)))
          } catch (e: Exception) {
            EventBus.getDefault().post(SubscribeFailure(FeedError(FeedError.UNEXPECTED)))
          }
        }
      }
    })
  }

  private fun executeFailure(t: Throwable) {
    val failure = if(t is IOException) SubscribeFailure(NetworkError(t))
    else SubscribeFailure(UnexpectedError())
    EventBus.getDefault().post(failure)
  }

  private fun extractBaseUrl(url: URL): String = "${url.protocol}://${url.host}/"

  private fun extractUrlPath(url: URL): String {

    if(url.path.isNotBlank() && url.path.first() == '/') return url.path.substring(1, url.path.length)
    return url.path
  }

  class UrlParsedHolder(val fullUrl: String, val baseUrl: String, val urlPath: String)

  class ResponseEventClass(val isSucceed: Boolean,
                           val body: List<ArticleEntity>?,
                           val targetUrl: String,
                           val code: Int)
}