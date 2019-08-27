package zz.utility.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_phone_launcher.*
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
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class PhoneLauncherActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_launcher)

        goto_draw.setOnClickListener { gotoNewWindow(TouchScreenActivity::class.java) }
        goto_osm_maps.setOnClickListener { gotoNewWindow(MapsActivity::class.java) }
        goto_files.setOnClickListener { gotoNewWindow(FileBrowserActivity::class.java) }
        goto_gps.setOnClickListener { gotoNewWindow(GPSActivity::class.java) }
        goto_main.setOnClickListener { gotoNewWindow(MainActivity::class.java) }
        goto_main.setOnLongClickListener { consume { displayDeviceInfo { updateApps() } } }

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        updateApps()
        screen_text.text = "Last started at ${Date().fullDate()}"
    }

    override fun onBackPressed() {
//        scroll.fullScroll(View.FOCUS_UP)
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

            val phone = apps.find { it.packageName == config.o("dedicated").s("phone") }
            val whatsapp = apps.find { it.packageName == config.o("dedicated").s("messaging1") }
            val telegram = apps.find { it.packageName == config.o("dedicated").s("messaging2") }
            val browser = apps.find { it.packageName == config.o("dedicated").s("browser") }
            val music = apps.find { it.packageName == config.o("dedicated").s("music") }

            val faves = ArrayList<String>().apply {
                if (phone != null) add(phone.packageName)
                if (whatsapp != null) add(whatsapp.packageName)
                if (telegram != null) add(telegram.packageName)
                if (browser != null) add(browser.packageName)
                if (music != null) add(music.packageName)
            }

            val list = apps
                    .filter { !hidden.contains(it.packageName) && !faves.contains(it.packageName) && it.packageName != "zz.utility" }
                    .sortedWith(Comparator { o1, o2 -> o1.label.compareTo(o2.label, true) })

            runOnUiThread {
                main_grid.removeAllViews()
                list.forEach { addToGrid(it) }

                if (phone != null) {
                    open_phone.see()
                    open_phone.setOnClickListener { runApp(phone) }
                }

                if (whatsapp != null) {
                    open_whatsapp.see()
                    open_whatsapp.setOnClickListener { runApp(whatsapp) }
                }

                if (telegram != null) {
                    open_telegram.see()
                    open_telegram.setOnClickListener { runApp(telegram) }
                }

                if (browser != null) {
                    open_browser.see()
                    open_browser.setOnClickListener { runApp(browser) }
                }

                if (music != null) {
                    open_music.see()
                    open_music.setOnClickListener { runApp(music) }
                }

                if (faves.isEmpty()) {
                    shortcuts_bottom.unsee()
                }

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
        im.setOnClickListener { runApp(app) }
        main_grid.addView(im)
    }

    private fun runApp(app: AppInfo) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        startActivity(packageManager.getLaunchIntentForPackage(app.packageName))
    }
}
