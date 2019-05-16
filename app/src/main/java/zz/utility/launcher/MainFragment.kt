package zz.utility.launcher

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_launcher_main.view.*
import kotlinx.android.synthetic.main.launcher_icon.view.*
import zz.utility.BuildConfig
import zz.utility.MAIN
import zz.utility.R
import zz.utility.helpers.*


class MainFragment : androidx.fragment.app.Fragment() {
    private lateinit var stats: TextView
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_launcher_main, null)


        val config = MAIN.fileAsJsonObject().o("launcher")
        val favourites = config.a("fave").map { it.asString }
        val hidden = config.a("hide").map { it.asString }
        val pm = activity!!.packageManager

        view.grid_all_apps.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 3 else 2
        view.grid.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 8 else 5

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0).asSequence().map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName) }

        favourites.forEach { fave ->
            val p = allApps.firstOrNull { it.packageName == fave }
            if (p != null) {
                val im = layoutInflater.inflate(R.layout.launcher_icon_main, view.grid, false) as TextView
                im.text = p.label.firstLetters()
                im.setOnLongClickListener { consume { context?.toast(p.label) } }
                im.setOnClickListener { context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(p.packageName)) }
                view.grid.addView(im)
            }
        }

        val appsList: Array<AppInfo> = allApps.filter {
            !hidden.contains(it.packageName) && it.packageName != "zz.utility" && it.packageName != "com.android.vending"
        }.toList().toTypedArray()

        appsList.sortWith(Comparator { o1, o2 ->
            o1.label.compareTo(o2.label, true)
        })

        appsList.forEach { app ->
            val im = layoutInflater.inflate(R.layout.launcher_icon, view.grid_all_apps, false)
            im.title.text = app.label
            im.subtitle.text = app.packageName
            im.img.text = app.label.firstLetters().toUpperCase()
            im.setOnLongClickListener { consume { context?.toast(app.label) } }
            im.setOnLongClickListener {
                consume { context!!.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + app.packageName))) }
            }
            im.setOnClickListener { context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(app.packageName)) }
            view.grid_all_apps.addView(im)
        }

        view.play_store_link.setOnClickListener {
            context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage("com.android.vending"))
        }

        view.this_link.setOnLongClickListener {
            consume { context!!.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))) }
        }

        view.this_link.setOnClickListener {
            val a = activity ?: return@setOnClickListener
            val batteryIntent = a.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
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

            val actManager = a.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)

            val mif = a.getFreeInternalMemory()
            val mit = a.getTotalInternalMemory()
            val f = a.getFreeExternalMemory()
            val t = a.getTotalExternalMemory()
            val o = (1 until f.size).joinToString("\n") { "External: ${(t[it] - f[it]).formatSize()} / ${t[it].formatSize()}" }
            val text = """Battery: $battery% | $temp°
                |Battery: $status $plugged
                |Memory: ${(memInfo.totalMem - memInfo.availMem).formatSize()} / ${memInfo.totalMem.formatSize()}
                |Internal: ${(mit - mif).formatSize()} / ${mit.formatSize()}
                |$o
            """.trimMargin()
            a.alert(text)
        }

        return view
    }
}