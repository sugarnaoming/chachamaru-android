package com.sugarnaoming.chachamaru

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ShareCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.sugarnaoming.chachamaru.Datamodel.ArticleEntity

class ArticlesAdapter(private val articles: List<ArticleEntity>, private val activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun getItemCount(): Int = articles.size

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
    val articleViewHolder = holder as ArticleViewHolder
    articleViewHolder.title.text = articles[position].title
    articleViewHolder.linkUrl = articles[position].link
    val parsedHtml = HTMLParser().removeImgTag(articles[position].description)
    articleViewHolder.description.text = fromHtml(parsedHtml)
    articleViewHolder.shareButton.setOnClickListener {
      val builder = ShareCompat.IntentBuilder.from(activity)
      builder.run {
        setChooserTitle(R.string.chose_send_app)
        setSubject(articles[position].title)
        setText(articles[position].link)
        setType("text/plain")
      }
      builder.startChooser()
    }
    articleViewHolder.articleOverView.setOnClickListener {
      val uri = Uri.parse(articles[position].link)
      val intent = Intent(Intent.ACTION_VIEW, uri)
      activity.startActivity(intent)
    }
  }


  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
    val itemView = LayoutInflater.from(parent!!.context).inflate(R.layout.article, parent, false)
    return ArticleViewHolder(itemView)
  }

  private fun fromHtml(html: String): String {
    val result = if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
      Html.fromHtml(html)
    }
    return result.toString()
  }

  private class ArticleViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {
    val title = itemView!!.findViewById<TextView>(R.id.article_title)
    var linkUrl = ""
    val description = itemView!!.findViewById<TextView>(R.id.article_description)
    val shareButton = itemView!!.findViewById<ImageButton>(R.id.shareButton)
    val articleOverView = itemView!!.findViewById<LinearLayout>(R.id.articleOverView)
  }
}

