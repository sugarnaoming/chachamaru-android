package com.sugarnaoming.chachamaru

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.sugarnaoming.chachamaru.MainActivity.Companion.IS_RE_DRAWER
import com.sugarnaoming.chachamaru.errors.TabError
import com.sugarnaoming.chachamaru.errors.UnexpectedError
import com.sugarnaoming.chachamaru.errors.UrlParseError
import com.sugarnaoming.chachamaru.model.database.DatabaseController
import com.sugarnaoming.chachamaru.errors.Errors
import kotlinx.android.synthetic.main.activity_tab_add.*
import java.net.MalformedURLException
import java.net.URL

class TabAddActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tab_add)
    setSupportActionBar(tab_add_tool_bar)
    title = resources.getText(R.string.tab_add)
    supportActionBar!!.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }
    val tabName = findViewById<EditText>(R.id.add_tab_name)
    val tabUrl = findViewById<EditText>(R.id.add_tab_url)
    val choseContentType: RadioGroup = findViewById(R.id.chose_content_type_radio_group_add_activity)
    findViewById<Button>(R.id.tab_add_ok_button).setOnClickListener {
      if(tabName.text.toString().isNotBlank() && tabUrl.text.toString().isNotBlank()) {
        val groupName = ApplicationDataHolder.groupName
        val dbController = DatabaseController(ApplicationDataHolder.appContext!!)
        if(dbController.howManyTabNamesAreInGroup(groupName, tabName.text.toString()) == 0) {
          val isRss = when(choseContentType.checkedRadioButtonId) {
            R.id.rss_add_radio_button -> true
            R.id.json_add_radio_button -> false
            else -> false
          }
          var isUrlParseSucceed = true
          try {
            URL(tabUrl.text.toString())
          }catch (e: MalformedURLException) {
            isUrlParseSucceed = false
            Errors().showMessage(this, UrlParseError())
          }catch (e: Exception) {
            isUrlParseSucceed = false
            Errors().showMessage(this, UnexpectedError())
          }
          if(isUrlParseSucceed) {
            dbController.addUrl(groupName, tabName.text.toString(), tabUrl.text.toString(), isRss)
            activityFinish()
          }
        } else {
          Errors().showMessage(this, TabError(TabError.NAME_DUPLICATE))
        }
      } else {
        Errors().showMessage(this, TabError(TabError.NAME_OR_URL_BLANK))
      }
    }
    findViewById<Button>(R.id.tab_add_cancel_button).setOnClickListener { finish() }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when(item!!.itemId) {
      android.R.id.home -> finish()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun activityFinish() {
    setResult(IS_RE_DRAWER)
    finish()
  }
}
