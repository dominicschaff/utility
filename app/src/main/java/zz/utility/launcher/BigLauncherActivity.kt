package zz.utility.launcher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_big_launcher.*
import kotlinx.android.synthetic.main.launcher_big_app.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import zz.utility.MAIN_CONFIG
import zz.utility.MainActivity
import zz.utility.R
import zz.utility.browser.FileBrowserActivity
import zz.utility.helpers.*
import zz.utility.maps.MapsActivity
import zz.utility.utility.GPSActivity
import zz.utility.utility.TouchScreenActivity

class BigLauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_launcher)

        goto_draw.setOnClickListener { goto(TouchScreenActivity::class.java) }
        goto_osm_maps.setOnClickListener { gotoNewWindow(MapsActivity::class.java) }
        goto_files.setOnClickListener { gotoNewWindow(FileBrowserActivity::class.java) }
        goto_gps.setOnClickListener { gotoNewWindow(GPSActivity::class.java) }
        goto_main.setOnClickListener { gotoNewWindow(MainActivity::class.java) }
        goto_main.setOnLongClickListener { consume { displayDeviceInfo { updateApps() } } }

        updateApps()
    }

    override fun onBackPressed() {
        scroll.fullScroll(View.FOCUS_UP)
    }

    private fun updateApps() {
        GlobalScope.launch {
            val config = MAIN_CONFIG.o("launcher")
            val hidden = config.a("hide").map { it.asString }
            val pm = packageManager

            val i = Intent(Intent.ACTION_MAIN, null)
            i.addCategory(Intent.CATEGORY_LAUNCHER)

            val apps = pm.queryIntentActivities(i, 0)
                    .map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName) }

            val list = apps
                    .filter { !hidden.contains(it.packageName) && it.packageName != "zz.utility" }
                    .sortedWith(Comparator { o1, o2 -> o1.label.compareTo(o2.label, true) })

            runOnUiThread {
                main_grid.removeAllViews()
                list.forEach { addToGrid(it) }
            }
        }
    }

    private fun addToGrid(app: AppInfo) {
        val im = layoutInflater.inflate(R.layout.launcher_big_app, main_grid, false)
        im.title.text = app.label
        im.img.text = app.label.firstLetters().toUpperCase()
        im.setOnLongClickListener {
            consume { startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + app.packageName))) }
        }
        im.setOnClickListener { startActivity(packageManager.getLaunchIntentForPackage(app.packageName)) }
        main_grid.addView(im)
    }
}
