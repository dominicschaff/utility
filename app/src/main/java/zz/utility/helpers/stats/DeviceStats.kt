package zz.utility.helpers.stats

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.telephony.TelephonyManager

class DeviceStats {
    var deviceSerial: String = ""
    var battery: Float = 0.toFloat()
    var manufacturer: String = ""
    var brand: String = ""
    var model: String = ""
    var device: String = ""
    var display: String = ""
    var product: String = ""
    var width: Int = 0
    var height: Int = 0
    var simSerialNumber: String = ""
    var deviceIdNumber: String = ""
    var uptime: Long = 0
    var battery_temperature: Double = 0.toDouble()

    companion object {

        @SuppressLint("HardwareIds", "MissingPermission")
        operator fun get(activity: Activity): DeviceStats {
            val ds = DeviceStats()

            ds.deviceSerial = android.os.Build.SERIAL
            val batteryIntent = activity.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val temp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)

            ds.battery = if (level == -1 || scale == -1) 50.0f else level.toFloat() / scale.toFloat() * 100.0f
            ds.battery_temperature = temp / 10.0
            ds.manufacturer = Build.MANUFACTURER
            ds.model = Build.MODEL
            ds.brand = Build.BRAND
            ds.device = Build.DEVICE
            ds.display = Build.DISPLAY
            ds.product = Build.PRODUCT

            ds.uptime = SystemClock.uptimeMillis()

            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            ds.width = size.x
            ds.height = size.y

            val telManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            ds.simSerialNumber = telManager.simSerialNumber
            ds.deviceIdNumber = telManager.deviceId
            return ds
        }
    }
}
