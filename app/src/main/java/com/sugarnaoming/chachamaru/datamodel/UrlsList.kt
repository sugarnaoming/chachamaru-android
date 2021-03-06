package com.sugarnaoming.chachamaru.datamodel

import android.os.Bundle
import java.io.Serializable

class UrlsList(val list: List<ArticleConnectionEntity>): Serializable {
  fun getBundle(): Bundle {
    val bundle = Bundle()
    bundle.putSerializable("urlslist", this)
    return bundle
  }
}