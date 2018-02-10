package com.sugarnaoming.chachamaru

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.sugarnaoming.chachamaru.MainActivity.Companion.IS_RE_DRAWER
import com.sugarnaoming.chachamaru.errors.GroupError
import com.sugarnaoming.chachamaru.model.database.DatabaseController
import com.sugarnaoming.chachamaru.errors.Errors
import kotlinx.android.synthetic.main.activity_edit_group.*

class GroupEditActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_group)
    setSupportActionBar(group_edit_tool_bar)
    title = resources.getText(R.string.group_edit)
    supportActionBar!!.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
    }
    val groupName: EditText = findViewById(R.id.edit_group_name)
    groupName.setText(ApplicationDataHolder.groupName)
    findViewById<Button>(R.id.group_edit_cancel_button).setOnClickListener { finish() }
    findViewById<Button>(R.id.group_edit_ok_button).setOnClickListener {
      if(groupName.text.toString().isNotBlank()) {
        val dbController = DatabaseController(this)
        val groupList = dbController.getAllGroupList()
        if(!groupList.contains(groupName.text.toString())) {
            DatabaseController(this)
                .updateGroup(groupName.text.toString(), ApplicationDataHolder.groupName)
          ApplicationDataHolder.groupName = groupName.text.toString()
          activityFinish()
        } else {
          Errors().showMessage(this, GroupError(GroupError.NAME_DUPLICATE))
        }
      } else {
        Errors().showMessage(this, GroupError(GroupError.NAME_BLANK))
      }
    }
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
