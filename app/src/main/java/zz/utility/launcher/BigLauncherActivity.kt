package zz.utility.launcher

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_big_launcher.*
import kotlinx.android.synthetic.main.launcher_big_app.view.*
import zz.utility.MAIN
import zz.utility.MainActivity
import zz.utility.R
import zz.utility.browser.FileBrowserActivity
import zz.utility.helpers.*
import zz.utility.maps.MapsActivity
import zz.utility.utility.BarcodeScanningActivity
import zz.utility.utility.GPSActivity

class BigLauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_launcher)

        goto_scan.setOnClickListener { gotoNewWindow(BarcodeScanningActivity::class.java) }
        goto_osm_maps.setOnClickListener { gotoNewWindow(MapsActivity::class.java) }
        goto_files.setOnClickListener { gotoNewWindow(FileBrowserActivity::class.java) }
        goto_gps.setOnClickListener { gotoNewWindow(GPSActivity::class.java) }
        goto_main.setOnClickListener { gotoNewWindow(MainActivity::class.java) }


        val config = MAIN.fileAsJsonObject().o("launcher")
        val favourites = config.a("fave").map { it.asString }
        val hidden = config.a("hide").map { it.asString }
        val pm = packageManager

        main_grid.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 6 else 4

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0).asSequence().map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName) }


        val sortedApps = allApps.sortedWith(Comparator { o1, o2 ->
            o1.label.compareTo(o2.label, true)
        })
        sortedApps.filter { favourites.contains(it.packageName) }.forEach(this::addToGrid)

        sortedApps.filter {
            !hidden.contains(it.packageName) && it.packageName != "zz.utility" && !favourites.contains(it.packageName)
        }.forEach(this::addToGrid)

        goto_main.setOnLongClickListener {
            val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val temp = (batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0).toInt()
            val status = when (batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "fully charged"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not charging"
                else -> "Unknown"
            }
            val plugged = when (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                1 -> "on AC"
                2 -> "on USB"
                0 -> "unplugged"
                4 -> "on Wireless"
                else -> "unknown"
            }

            val battery = (if (level == -1 || scale == -1) 50.0f else level.toFloat() / scale.toFloat() * 100.0f).toInt()

            val actManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)

            val mif = getFreeInternalMemory()
            val mit = getTotalInternalMemory()
            val f = getFreeExternalMemory()
            val t = getTotalExternalMemory()
            val o = (1 until f.size).joinToString("\n") { "External: ${(t[it] - f[it]).formatSize()} / ${t[it].formatSize()}" }
            val text = """Battery: $battery% | $temp°
                |Battery: $status $plugged
                |Memory: ${(memInfo.totalMem - memInfo.availMem).formatSize()} / ${memInfo.totalMem.formatSize()}
                |Internal: ${(mit - mif).formatSize()} / ${mit.formatSize()}
                |$o
            """.trimMargin()
            alert(text)
            true
        }
    }

    override fun onBackPressed() {
        scroll.fullScroll(View.FOCUS_UP)
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
