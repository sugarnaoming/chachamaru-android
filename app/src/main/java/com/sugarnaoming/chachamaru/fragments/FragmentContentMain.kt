package com.sugarnaoming.chachamaru.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sugarnaoming.chachamaru.ApplicationDataHolder
import com.sugarnaoming.chachamaru.datamodel.ArticleConnectionEntity
import com.sugarnaoming.chachamaru.R
import com.sugarnaoming.chachamaru.datamodel.BadRequestException
import com.sugarnaoming.chachamaru.model.Cache
import com.sugarnaoming.chachamaru.model.DatabaseController

class FragmentContentMain: android.support.v4.app.Fragment() {
  private lateinit var urlsList: List<ArticleConnectionEntity>
  private var isFistView = true
  private val dbController = DatabaseController(ApplicationDataHolder.appContext!!)

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return inflater!!.inflate(R.layout.fragment_content_main, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    urlsList = dbController.getUrlsByGroupNameOf(ApplicationDataHolder.groupName)
  }

  override fun onResume() {
    super.onResume()
    val isUpdated = if(isUpdate(urlsList)) {
      urlsList = DatabaseController(ApplicationDataHolder.appContext!!).getUrlsByGroupNameOf(ApplicationDataHolder.groupName)
      true
    } else {false}
    if(isFistView || isUpdated) {
      val tablayout = view!!.findViewById<TabLayout>(R.id.tabs)
      // 別のカテゴリに移動した時にキャッシュしているURLを削除
      Cache.clear()
      tablayout.tabMode = TabLayout.MODE_SCROLLABLE
      // 新規TABの生成
      if (!urlsList.isEmpty()) {
        for (i in 0..urlsList.size) {
          tablayout.addTab(tablayout.newTab().setText("Tab $i"))
        }
        val viewPager = view!!.findViewById<ViewPager>(R.id.pager)
        try {
          viewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            // 特定のTabが選ばれた時に生成されるフラグメントを返す
            override fun getItem(position: Int): Fragment {
              val fragment = FragmentContentArticle()
              fragment.arguments = urlsList[position].getBundle()
              return fragment
            }
            // タブの数を返す
            override fun getCount(): Int = urlsList.size
            // ページのタイトルを返す
            override fun getPageTitle(position: Int): CharSequence = urlsList[position].title
            // これを追加するとFragmentを再生成する
            override fun getItemPosition(`object`: Any?): Int = PagerAdapter.POSITION_NONE
          }
          tablayout.setupWithViewPager(viewPager)
          this.isFistView = false
        } catch (e: BadRequestException) {
          throw e
        } catch (e: Exception) {
          throw e
        }
      }
    }
  }

  private fun isUpdate(urlsList: List<ArticleConnectionEntity>): Boolean {
    val nowUrlsList = dbController.getUrlsByGroupNameOf(ApplicationDataHolder.groupName)
    if(urlsList.size != nowUrlsList.size) return true
    (0 until nowUrlsList.size step 1).forEach {
      when {
        nowUrlsList[it].groupName != urlsList[it].groupName -> return true
        nowUrlsList[it].title != urlsList[it].title -> return true
        nowUrlsList[it].url != urlsList[it].url -> return true
        nowUrlsList[it].isRssUrl != urlsList[it].isRssUrl -> return true
      }
    }
    return false
  }
}