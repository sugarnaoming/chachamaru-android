package com.sugarnaoming.chachamaru.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RssUrl {
  @GET
  fun getArticles(@Url rssUrl: String): Call<String>
}