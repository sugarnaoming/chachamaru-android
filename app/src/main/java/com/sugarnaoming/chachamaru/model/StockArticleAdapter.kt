package com.sugarnaoming.chachamaru.model

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ShareCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.sugarnaoming.chachamaru.R
import com.sugarnaoming.chachamaru.datamodel.ArticleStock

class StockArticleAdapter(private val stocks: MutableList<ArticleStock>, private val activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = stocks.size

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
    val articleViewHolder = holder as ArticleViewHolder
    articleViewHolder.title.text = stocks[position].title
    articleViewHolder.linkUrl = stocks[position].url
    articleViewHolder.description.text = stocks[position].description
    // descriptionが空の時にカラムの高さを0にして潰す
    if (articleViewHolder.description.text == "") {
      val layoutParams = articleViewHolder.description.layoutParams as ViewGroup.MarginLayoutParams
      layoutParams.height = 0
      layoutParams.topMargin = 0
      articleViewHolder.description.layoutParams = layoutParams
    }
    articleViewHolder.shareButton.setOnClickListener {
      val builder = ShareCompat.IntentBuilder.from(activity)
      builder.run {
        setChooserTitle(R.string.chose_send_app)
        setSubject(stocks[position].title)
        setText(stocks[position].url)
        setType("text/plain")
      }
      builder.startChooser()
    }
    articleViewHolder.articleOverView.run {
      setOnClickListener {
        val uri = Uri.parse(stocks[position].url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
      }
    }
  }

  fun remove(position: Int) {
    DatabaseController(activity).deleteStock(stocks[position])
    stocks.removeAt(position)
    notifyItemRemoved(position)
  }

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
    val itemView = LayoutInflater.from(parent!!.context).inflate(R.layout.article, parent, false)
    return ArticleViewHolder(itemView)
  }

  private class ArticleViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {
    val title = itemView!!.findViewById<TextView>(R.id.article_title)
    var linkUrl = ""
    val description = itemView!!.findViewById<TextView>(R.id.article_description)
    val shareButton = itemView!!.findViewById<ImageButton>(R.id.shareButton)
    val articleOverView = itemView!!.findViewById<LinearLayout>(R.id.articleOverView)
  }
}