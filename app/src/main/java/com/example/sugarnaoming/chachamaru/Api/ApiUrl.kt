package com.example.sugarnaoming.chachamaru.Api

import com.example.sugarnaoming.chachamaru.Datamodel.ArticleEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiUrl {
  @GET
  fun getArticles(@Url apiUrl: String): Call<List<ArticleEntity>>
}