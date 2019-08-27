package zz.utility.launcher

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.alert_info.view.*
import zz.utility.R
import zz.utility.helpers.*

data class AppInfo(
        val label: String,
        val packageName: String
)

fun CharSequence.firstLetters(): String {
    val a = this.split(" ")
    return when (a.size) {
        0 -> "-"
        1 -> "${a[0].first()}"
        else -> "${a[0].first()}${a[1].first()}"
    }
}

fun Activity.displayDeviceInfo(f:()->Unit) {
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
    val text = """Battery:  $battery% | $temp°
                |Battery:  $status $plugged
                |Memory:   ${(memInfo.totalMem - memInfo.availMem).formatSize()} / ${memInfo.totalMem.formatSize()}
                |Internal: ${(mit - mif).formatSize()} / ${mit.formatSize()}
                |$o
            """.trimMargin()

    val l = layoutInflater.inflate(R.layout.alert_info, null)

    val dialog = AlertDialog.Builder(this)
    dialog.setView(l)
    dialog.setCancelable(true)
    val finalDialog = dialog.show()


    l.clear_storage.setOnClickListener {
        cacheDir.deleteRecursively()
        finalDialog.dismiss()
    }

    l.reload_apps.setOnClickListener { f() }

    l.device_info.text = text
}