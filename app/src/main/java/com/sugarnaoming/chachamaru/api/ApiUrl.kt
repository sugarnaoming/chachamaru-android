package com.sugarnaoming.chachamaru.api

import com.sugarnaoming.chachamaru.datamodel.ArticleEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiUrl {
  @GET
  fun getArticles(@Url apiUrl: String): Call<List<ArticleEntity>>
}