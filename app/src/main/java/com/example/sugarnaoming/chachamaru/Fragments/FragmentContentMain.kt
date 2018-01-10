package com.example.sugarnaoming.chachamaru.Fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sugarnaoming.chachamaru.R
import com.example.sugarnaoming.chachamaru.Datamodel.BadRequestException
import com.example.sugarnaoming.chachamaru.Datamodel.UrlsList

class FragmentContentMain: android.support.v4.app.Fragment() {
  lateinit var urlsList: UrlsList

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    this.urlsList = arguments.getSerializable("urlslist") as UrlsList
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return inflater!!.inflate(R.layout.fragment_content_main, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val tablayout = view!!.findViewById<TabLayout>(R.id.tabs)
    // 別のカテゴリに移動した時にキャッシュしているURLを削除
    FragmentContentArticle.clearCache()
    tablayout.tabMode = TabLayout.MODE_SCROLLABLE
    // 新規TABの生成
    for (i in 0..urlsList.list.size) {
      tablayout.addTab(tablayout.newTab().setText("Tab $i"))
    }
    val viewPager = view.findViewById<ViewPager>(R.id.pager)
    try {
      viewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
        // 特定のTabが選ばれた時に生成されるフラグメントを返す
        override fun getItem(position: Int): Fragment {
          val fragment = FragmentContentArticle()
          fragment.arguments = urlsList.list[position].getBundle()
          return fragment
        }

        // タブの数を返す
        override fun getCount(): Int = urlsList.list.size

        // ページのタイトルを返す
        override fun getPageTitle(position: Int): CharSequence = urlsList.list[position].title

        // これを追加するとFragmentを再生成する
        override fun getItemPosition(`object`: Any?): Int = PagerAdapter.POSITION_NONE
      }
      tablayout.setupWithViewPager(viewPager)
    } catch (e: BadRequestException) { throw e
    } catch (e: Exception) { throw e }
  }
}