package com.sugarnaoming.chachamaru

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import com.sugarnaoming.chachamaru.fragments.FragmentContentMain
import com.sugarnaoming.chachamaru.errors.BadRequestException
import com.sugarnaoming.chachamaru.model.DatabaseController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  private lateinit var fragmentManager:FragmentManager
  private lateinit var currentGroupItem: MenuItem

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    this.fragmentManager = supportFragmentManager

    ApplicationDataHolder.appContext = applicationContext

    updateNavigationMenuInGroup()

    val toggle = ActionBarDrawerToggle(
        this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    drawer_layout.addDrawerListener(toggle)
    toggle.syncState()
    nav_view.setNavigationItemSelectedListener(this)
    try {
      firstViewFragment()
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when {
      requestCode == SELECTED_OPTION_MENU && resultCode == IS_RE_DRAWER -> {
        updateNavigationMenuInGroup()
        // フラグメントを再描画しないと記事のタイトルだけ表示される問題が起きる
        fragmentReplace(ApplicationDataHolder.groupName)
      }
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when(item.title.toString()) {
      resources.getString(R.string.about_this_app) -> intent(this, AboutThisAppActivity::class.java, false)
      resources.getString(R.string.read_later) -> intent(this, StockActivity::class.java, false)
      else -> {
        this.currentGroupItem = item
        try {
          fragmentReplace(item.title.toString())
        } catch (e: BadRequestException) {
          createExceptionDialog(e)
        } catch (e: Exception) {
          createExceptionDialog(e)
        }
      }
    }
    drawer_layout.closeDrawer(GravityCompat.START)
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.config_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when(item!!.itemId) {
      R.id.tab_add -> intent(this, TabAddActivity::class.java, true)
      R.id.tab_edit -> intent(this, TabEditActivity::class.java, true)
      R.id.tab_delete -> deleteTab()
      R.id.group_add -> intent(this, GroupAddActivity::class.java, true)
      R.id.group_edit -> intent(this, GroupEditActivity::class.java, true)
      R.id.group_delete -> deleteGroup()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun deleteTab() {
    deleteDialogCreate(R.string.tab_delete, R.string.do_delete_current_tab, { _, _ ->
        DatabaseController(applicationContext).run {
          deleteUrl(ApplicationDataHolder.tabName, ApplicationDataHolder.tabUrl, ApplicationDataHolder.groupName)
          fragmentReplace(ApplicationDataHolder.groupName)
        }
    })
  }

  private fun deleteGroup() {
    deleteDialogCreate(R.string.group_delete, R.string.do_delete_current_group, { _, _ ->
        val dbController = DatabaseController(applicationContext)
        val urls = dbController.getUrlsByGroupNameOf(ApplicationDataHolder.groupName)
        // グループを0にはできない
        if(dbController.getAllGroupList().size > 1) {
          // グループに所属しているタブがある場合は警告を出す
          if (!urls.isEmpty()) {
            deleteDialogCreate(R.string.confirmation,
                R.string.there_are_tabs_belonging_to_the_group_you_are_deleting_do_you_really_want_to_delete_this,
                { _, _ ->
                  dbController.run {
                    deleteGroup(ApplicationDataHolder.groupName)
                    val groups = updateNavigationMenuInGroup()
                    fragmentReplace(groups.first())
                  }
                })
          } else {
            dbController.run {
              deleteGroup(ApplicationDataHolder.groupName)
              val groups = updateNavigationMenuInGroup()
              fragmentReplace(groups.first())
            }
          }
        } else {
          alertDialogCreate(R.string.error, R.string.the_number_of_group_can_not_be_zero)
        }
    })
  }

  private fun setTextColorForMenuItem(menuItem: MenuItem, @ColorRes color: Int) {
    val spanStr = SpannableString(menuItem.title.toString())
    spanStr.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanStr.length, 0)
    menuItem.title = spanStr
  }

  private fun resetAllMenuItemsTextColor(navigationView: NavigationView) {
    for (i in 0 until navigationView.menu.size())
      setTextColorForMenuItem(navigationView.menu.getItem(i), R.color.navigationViewTextColorPrimary)
  }

  private fun updateNavigationMenuInGroup(): List<String> {
    val leftMenu = findViewById<NavigationView>(R.id.nav_view)
    leftMenu.menu.clear()
    // 「このアプリについて」を追加
    addNavigationMenuItem(leftMenu.menu, R.id.left_menu_option_group, listOf(resources.getString(R.string.about_this_app)))
    addNavigationMenuItem(leftMenu.menu, R.id.left_menu_option_group, listOf(resources.getString(R.string.read_later)))
    val menuTitles = DatabaseController(applicationContext).getAllGroupList()
    // グループをmenuに追加する
    addNavigationMenuItem(leftMenu.menu, R.id.left_menu_group, menuTitles)
    resetAllMenuItemsTextColor(leftMenu)
    return menuTitles
  }

  private fun addNavigationMenuItem(menu: Menu, groupId: Int, additionalItems: List<String>) {
    additionalItems.forEachIndexed { index, item ->
      menu.add(groupId, Menu.NONE, index, item)
    }
  }

  private fun intent(context: Context, cls: Class<*>, isSelectOptionMenu: Boolean) {
    if(isSelectOptionMenu) {
      startActivityForResult(Intent(context, cls), SELECTED_OPTION_MENU)
    } else {
      startActivity(Intent(context, cls))
    }
  }

  private fun alertDialogCreate(title: Int, message: Int) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(R.string.ok__alert_dialog, null)
        .show()
  }

  private fun deleteDialogCreate(title: Int, message: Int, positiveExecuteFun: (dialogInterface: DialogInterface, i: Int) -> Unit ) {
    AlertDialog.Builder(this)
          .setTitle(title)
          .setMessage(message)
          .setPositiveButton(R.string.ok__alert_dialog, { dialogInterface, i ->
            positiveExecuteFun(dialogInterface, i)
          })
          .setNegativeButton(R.string.cancel__alert_dialog, null)
          .show()
  }

  private fun createExceptionDialog(e: Throwable) {
    val alert = AlertDialog.Builder(this)
    alert.run {
      setMessage(e.message)
      setPositiveButton("OK", { _, _ -> })
      show()
    }
  }

  private fun fragmentReplace(groupName: String) {
    resetAllMenuItemsTextColor(findViewById(R.id.nav_view))
    val navView: NavigationView = findViewById(R.id.nav_view)
    for(i in 0 until navView.menu.size()) {
      if(navView.menu.getItem(i).title.toString() == groupName) {
        setTextColorForMenuItem(findViewById<NavigationView>(R.id.nav_view).menu.getItem(i), R.color.colorNavigationViewSelectedText)
        break
      }
    }
    ApplicationDataHolder.groupName = groupName
    title = ApplicationDataHolder.groupName
    val fragment = FragmentContentMain() as android.support.v4.app.Fragment
    val transaction = this.fragmentManager.beginTransaction()
    transaction.replace(R.id.fragment_container, fragment)
    transaction.commitNow()
  }

  private fun firstViewFragment() {
    resetAllMenuItemsTextColor(findViewById(R.id.nav_view))
    setTextColorForMenuItem(findViewById<NavigationView>(R.id.nav_view).menu.getItem(2), R.color.colorNavigationViewSelectedText)
    // DBに登録されているグループの中で最初のグループを初回に表示する
    ApplicationDataHolder.groupName = DatabaseController(applicationContext).getAllGroupList().first()
    title = ApplicationDataHolder.groupName
    val fragment = FragmentContentMain() as android.support.v4.app.Fragment
    val transaction = this.fragmentManager.beginTransaction()
    transaction.add(R.id.fragment_container, fragment)
    transaction.commitNow()
  }

  companion object {
    const val SELECTED_OPTION_MENU = 1
    const val IS_RE_DRAWER = 1
  }
}
