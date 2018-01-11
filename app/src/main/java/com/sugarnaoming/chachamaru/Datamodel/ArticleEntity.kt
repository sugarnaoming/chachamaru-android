package com.sugarnaoming.chachamaru.Datamodel

import com.squareup.moshi.Json

data class ArticleEntity(@Json(name = "title") val title: String = "",
                         @Json(name = "link") val link: String = "",
                         @Json(name = "description") val description: String = "")