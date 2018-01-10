package com.example.sugarnaoming.chachamaru.Api

import com.example.sugarnaoming.chachamaru.Datamodel.ArticleEntity
import com.example.sugarnaoming.chachamaru.Datamodel.BadRequestException
import com.rometools.rome.io.SyndFeedInput
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.StringReader
import java.net.URL

class Api {
  private val API_SERVER_URL = "http://160.16.115.175"
  fun run(targetUrl: String, isRss: Boolean){
    if (isRss) receiveRss(targetUrl)
    else receiveApi(targetUrl)
  }

  private fun receiveApi(targetUrl: String) {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(this.API_SERVER_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: ApiUrl = retrofit.create(ApiUrl::class.java)
    service.getArticles(targetUrl).enqueue(object : Callback<List<ArticleEntity>> {
      override fun onFailure(call: Call<List<ArticleEntity>>?, t: Throwable?) {
        throw t!!
      }

      override fun onResponse(call: Call<List<ArticleEntity>>?, response: Response<List<ArticleEntity>>?) {
        if(!response!!.isSuccessful) throw BadRequestException(response.code())
        EventBus.getDefault().post(ResponseEventClass(response.isSuccessful, response.body()!!, targetUrl, response.code()))
      }
    })
  }

  private fun receiveRss(targetUrl: String) {
    val url = URL(targetUrl)
    val baseUrl = this.extractBaseUrl(url)
    val urlPath = this.extractUrlPath(url)
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    val service: RssUrl = retrofit.create(RssUrl::class.java)
    service.getArticles(urlPath).enqueue(object: Callback<String> {
      override fun onFailure(call: Call<String>?, t: Throwable?) {
        throw t!!
      }

      override fun onResponse(call: Call<String>?, response: Response<String>?) {
        if(!response!!.isSuccessful) throw BadRequestException(response.code())
        val feed = SyndFeedInput().build(StringReader(response.body()))
        val articles = feed.entries.map {
          val description = if(it.description == null) it.contents.first().value else it.description.value
          ArticleEntity(it.title, it.link, description)
        }
        EventBus.getDefault().post(ResponseEventClass(response.isSuccessful, articles, targetUrl, response.code()))
      }
    })
  }

  private fun extractBaseUrl(url: URL): String = "${url.protocol}://${url.host}/"

  private fun extractUrlPath(url: URL): String {
    if(url.path.first() == '/') return url.path.substring(1, url.path.length)
    return url.path
  }

  class ResponseEventClass(val isSucceed: Boolean,
                           val body: List<ArticleEntity>?,
                           val targetUrl: String,
                           val code: Int)
}