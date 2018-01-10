package com.example.sugarnaoming.chachamaru

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    val toggle = ActionBarDrawerToggle(
        this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    drawer_layout.addDrawerListener(toggle)
    toggle.syncState()

    nav_view.setNavigationItemSelectedListener(this)
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
    val urlsList =  dbController.getUrlsByGroupNameOf(item.title.toString())
    try {
      fragmentReplace(UrlsList(urlsList))
    }catch (e: BadRequestException) { createExceptionDialog(e)
    }catch (e: Exception) { createExceptionDialog(e) }
    drawer_layout.closeDrawer(GravityCompat.START)
    return true
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
