package com.sugarnaoming.chachamaru

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import com.sugarnaoming.chachamaru.MainActivity.Companion.IS_RE_DRAWER
import com.sugarnaoming.chachamaru.errors.TabError
import com.sugarnaoming.chachamaru.errors.UnexpectedError
import com.sugarnaoming.chachamaru.errors.UrlParseError
import com.sugarnaoming.chachamaru.model.DatabaseController
import com.sugarnaoming.chachamaru.model.Errors
import kotlinx.android.synthetic.main.activity_tab_edit.*
import java.net.MalformedURLException
import java.net.URL

class TabEditActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tab_edit)
    setSupportActionBar(tab_edit_tool_bar)
    title = resources.getText(R.string.tab_edit)
    supportActionBar!!.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }
    val tabName: EditText = findViewById(R.id.edit_tab_name)
    val tabUrl: EditText = findViewById(R.id.edit_tab_url)
    val choseContentType: RadioGroup = findViewById(R.id.chose_content_type_radio_group_edit_activity)
    tabName.setText(ApplicationDataHolder.tabName)
    tabUrl.setText(ApplicationDataHolder.tabUrl)
    if(ApplicationDataHolder.isRss) {
      findViewById<RadioButton>(R.id.rss_edit_radio_button).isChecked = true
    } else {
      findViewById<RadioButton>(R.id.json_edit_radio_button).isChecked = true
    }
    findViewById<Button>(R.id.tab_edit_cancel_button).setOnClickListener { finish() }
    findViewById<Button>(R.id.tab_edit_ok_button).setOnClickListener {
      val isRss = when(choseContentType.checkedRadioButtonId) {
            R.id.rss_edit_radio_button -> true
            R.id.json_edit_radio_button -> false
            else -> false
      }
      if(isDiffCurrentNameAndUrlAndRss(tabName.text.toString(), tabUrl.text.toString(), isRss)) {
        if(tabName.text.toString().isNotBlank() && tabUrl.text.toString().isNotBlank()) {
          val dbController = DatabaseController(this)
          if(dbController.howManyTabNamesAreInGroup(ApplicationDataHolder.groupName, tabName.text.toString()) == 0) {
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
              DatabaseController(applicationContext).updateUrl(tabName.text.toString(),
                  tabUrl.text.toString(), isRss, ApplicationDataHolder.tabName, ApplicationDataHolder.groupName)
              activityFinish()
            }
          } else {
            Errors().showMessage(this, TabError(TabError.NAME_DUPLICATE))
          }
        } else {
          Errors().showMessage(this, TabError(TabError.NAME_OR_URL_BLANK))
        }
      } else {
        finish()
      }
    }

  }
  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when(item!!.itemId) {
      android.R.id.home -> finish()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun isDiffCurrentNameAndUrlAndRss(name: String, url: String, isRss: Boolean): Boolean {
    if(name == ApplicationDataHolder.tabName && url == ApplicationDataHolder.tabUrl && ApplicationDataHolder.isRss == isRss ) return false
    return true
  }

  private fun activityFinish() {
    setResult(IS_RE_DRAWER)
    finish()
  }
}
