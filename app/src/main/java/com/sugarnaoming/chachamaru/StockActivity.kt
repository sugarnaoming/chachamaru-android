package com.sugarnaoming.chachamaru

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MenuItem
import com.sugarnaoming.chachamaru.datamodel.ArticleStock
import com.sugarnaoming.chachamaru.model.DatabaseController
import com.sugarnaoming.chachamaru.model.StockArticleAdapter
import kotlinx.android.synthetic.main.activity_stock.*



class StockActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_stock)
    setSupportActionBar(stock_tool_bar)
    title = resources.getText(R.string.read_later)
    supportActionBar!!.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }
  }

  override fun onStart() {
    super.onStart()
    val stock: MutableList<ArticleStock> = mutableListOf()
    val articles = DatabaseController(this).getAllStock()
    articles.forEach { stock.add(it) }
    val stockAdapter = StockArticleAdapter(stock, this)
    val recyclerView = findViewById<RecyclerView>(R.id.stockRecyclerView)
    recyclerView.run {
      layoutManager = LinearLayoutManager(context)
      adapter = stockAdapter
      isScrollbarFadingEnabled = true
    }

    val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
      override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
      }
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 横にスワイプされたら要素を消す
        val swipedPosition = viewHolder.adapterPosition
        val adapter = recyclerView.adapter as StockArticleAdapter
        adapter.remove(swipedPosition)
      }
    }
    ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when(item!!.itemId) {
      android.R.id.home -> finish()
    }
    return super.onOptionsItemSelected(item)
  }
}
