package com.sugarnaoming.chachamaru.datamodel

import android.os.Bundle
import java.io.Serializable

data class ArticleConnectionEntity(
    val id: Int,
    val title: String,
    val groupName: String,
    val url: String,
    val isRssUrl: Boolean
): Serializable {
  fun getBundle(): Bundle {
    val bundle = Bundle()
    bundle.putSerializable("ArticleConnectionInfo", this)
    return bundle
  }
}