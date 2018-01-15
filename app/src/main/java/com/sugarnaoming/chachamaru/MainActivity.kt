package com.sugarnaoming.chachamaru

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.sugarnaoming.chachamaru.Fragments.FragmentContentMain
import com.sugarnaoming.chachamaru.Datamodel.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  lateinit var fragmentManager:FragmentManager
  lateinit var dbController: DatabaseController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    this.fragmentManager = supportFragmentManager

    val leftMenu = findViewById<NavigationView>(R.id.nav_view).menu

    this.dbController = DatabaseController(applicationContext)
    val menuTitles = dbController.getGroupNamesWhereUrlExists()
    // menuに動的追加する
    menuTitles.forEachIndexed { index, groupName ->
      // p2の数値(index)が順番を表している。これを入れないと表示される順番が保証されない。
      leftMenu.add(R.id.left_menu_group, Menu.NONE, index, groupName)
    }
    val toggle = ActionBarDrawerToggle(
        this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    drawer_layout.addDrawerListener(toggle)
    toggle.syncState()
    nav_view.setNavigationItemSelectedListener(this)
    try {
      firstViewFragment(UrlsList(dbController.getUrlsByGroupNameOf(menuTitles.first())))
    }catch (e: BadRequestException) { createExceptionDialog(e)
    }catch (e: Exception) {createExceptionDialog(e) }
  }

  override fun onBackPressed() {
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    // Handle navigation view item clicks here.
    if(item.itemId == R.id.about_this_app) {
      this.createLicensesView()
    } else {
      val urlsList = dbController.getUrlsByGroupNameOf(item.title.toString())
      try {
        fragmentReplace(UrlsList(urlsList))
      } catch (e: BadRequestException) {
        createExceptionDialog(e)
      } catch (e: Exception) {
        createExceptionDialog(e)
      }
      drawer_layout.closeDrawer(GravityCompat.START)
    }
    return true
  }

  private fun createLicensesView() {
    LibsBuilder()
        .withLibraries("rome")
        .withActivityTitle(getString(R.string.about_this_app))
        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
        .start(this)
  }

  private fun createExceptionDialog(e: Throwable) {
    val alert = AlertDialog.Builder(this)
    alert.run {
      setMessage(e.message)
      setPositiveButton("OK", { dialogInterface, i -> })
      show()
    }
  }

  private fun fragmentReplace(urlsList: UrlsList) {
    val fragment = FragmentContentMain() as android.support.v4.app.Fragment
    fragment.arguments = urlsList.getBundle()
    val transaction = this.fragmentManager.beginTransaction()
    transaction.replace(R.id.fragment_container, fragment)
    transaction.commitNow()
  }

  private fun firstViewFragment(urlsList: UrlsList) {
    val fragment = FragmentContentMain() as android.support.v4.app.Fragment
    fragment.arguments = urlsList.getBundle()
    val transaction = this.fragmentManager.beginTransaction()
    transaction.add(R.id.fragment_container, fragment)
    transaction.commitNow()
  }
}
