package com.sugarnaoming.chachamaru.model.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.sugarnaoming.chachamaru.ApplicationDataHolder
import com.sugarnaoming.chachamaru.datamodel.ArticleEntity
import com.sugarnaoming.chachamaru.R
import com.sugarnaoming.chachamaru.datamodel.ArticleStock
import com.sugarnaoming.chachamaru.errors.Errors
import com.sugarnaoming.chachamaru.errors.StockError
import com.sugarnaoming.chachamaru.model.database.DatabaseController
import com.sugarnaoming.chachamaru.model.HTMLParser

class ArticlesAdapter(private val articles: List<ArticleEntity>, private val activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun getItemCount(): Int = articles.size

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
    val articleViewHolder = holder as ArticleViewHolder
    articleViewHolder.title.text = articles[position].title
    articleViewHolder.linkUrl = articles[position].link
    val parsedHtml = HTMLParser().removeImgTag(articles[position].description)
    articleViewHolder.description.text = fromHtml(parsedHtml)
    // descriptionが空の時にカラムの高さを0にして潰す
    if(articleViewHolder.description.text == "") {
      val layoutParams = articleViewHolder.description.layoutParams as ViewGroup.MarginLayoutParams
      layoutParams.height = 0
      layoutParams.topMargin = 0
      articleViewHolder.description.layoutParams = layoutParams
    }
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
    articleViewHolder.articleOverView.run {
      setOnClickListener {
        val uri = Uri.parse(articles[position].link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
      }
      setOnLongClickListener {
        val popupWindow = PopupWindow(activity)
        val popupView = LayoutInflater.from(activity).inflate(R.layout.article_option_menu, null)
        popupView.findViewById<TextView>(R.id.readLater).setOnClickListener {
          val article = ArticleStock(ApplicationDataHolder.groupName,
                  articleViewHolder.title.text.toString(),
                  articleViewHolder.description.text.toString(),
                  articleViewHolder.linkUrl)
          val dbController = DatabaseController(activity)
          if(dbController.isNotDuplicateArticleInStock(article)) {
            dbController.addStock(article)
            popupWindow.dismiss()
          } else {
            popupWindow.dismiss()
            val error = Errors()
            error.showMessage(context, StockError())
          }
        }
        popupWindow.run {
          contentView = popupView
          isOutsideTouchable = true
          isFocusable = true
          isTouchable = true
          setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, android.R.color.transparent)))
        }
        popupWindow.showAsDropDown(it, 0, -it.height/2, Gravity.END)
        true
      }
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

