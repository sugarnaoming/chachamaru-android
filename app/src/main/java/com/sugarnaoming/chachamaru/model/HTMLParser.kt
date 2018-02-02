package com.sugarnaoming.chachamaru.model

import org.jsoup.Jsoup

class HTMLParser {
  fun removeImgTag(targetHtml: String): String {
    val html = "${HTML_PREFIX}$targetHtml${HTML_SUFFIX}"
    val document = Jsoup.parse(html)
    document.getElementsByTag("img").remove()
    document.getElementsByTag("a")
    val documentBodyHtml = document.body().toString()
    return documentBodyHtml.replaceFirst("<body>","").reversed().replaceFirst(">ydob/<", "").reversed()
  }
  companion object {
    private val HTML_PREFIX = "<html><head></head><body>"
    private val HTML_SUFFIX = "</body></html>"
  }
}