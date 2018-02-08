package com.sugarnaoming.chachamaru

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.android.synthetic.main.activity_about_this_app.*

class AboutThisAppActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_about_this_app)
    setSupportActionBar(about_this_app_tool_bar)
    title = resources.getText(R.string.about_this_app)
    supportActionBar!!.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }

    findViewById<Button>(R.id.licenseButton).setOnClickListener {
      this.createLicensesView()
    }
  }

  private fun createLicensesView() {
    LibsBuilder()
        .withLibraries("rome", "jsoup")
        .withActivityTitle(getString(R.string.license))
        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
        .start(this)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when(item!!.itemId) {
      android.R.id.home -> finish()
    }
    return super.onOptionsItemSelected(item)
  }
}
