package zz.utility.utility

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zz.utility.R
import zz.utility.databinding.ActivityInfoBinding
import zz.utility.helpers.formatSize
import zz.utility.helpers.stats.getDeviceStats
import zz.utility.helpers.stats.getMemoryStats
import zz.utility.helpers.stats.getNetworkStats
import zz.utility.helpers.stats.getStorageStats
import zz.utility.helpers.toTime
import java.util.ArrayList

fun Boolean.eng(): String = if (this) "yes" else "no"

class InfoActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityInfoBinding

            private lateinit var mSensorManager: SensorManager
    private val deviceSensors = ArrayList<Sensor>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ss = getStorageStats()
        val ns = getNetworkStats()
        val ms = getMemoryStats()
        val ds = getDeviceStats()

        var tmpStr = "${ss.internal.formatSize()} / ${ss.internalFull.formatSize()}"

        ss.external.indices.forEach {
            tmpStr += "\n${ss.external[it].formatSize()} / ${ss.externalFull[it].formatSize()}"
        }
        binding.deviceStorage.text = tmpStr

        binding.deviceTraffic.text = """
            Mobile: ${ns.mobileRx.formatSize()} / ${ns.mobileTx.formatSize()}
            App: ${ns.appRx.formatSize()} / ${ns.appTx.formatSize()}
            Total: ${ns.totalRx.formatSize()} / ${ns.totalTx.formatSize()}
        """.trimIndent()

        binding.deviceBattery.text = "Level %.1f %%\nTemperature %.1f °".format(ds.battery, ds.battery_temperature)

        binding.deviceMemory.text = "${(ms.total - ms.available).formatSize()} / ${ms.total.formatSize()} [${ms.threshold.formatSize()}]"

        binding.deviceNetwork.text = """
            Operator Name: ${ns.operatorName}
            Network State: ${ns.serviceStateDescription}
            Network Strength: ${"${ns.signalStrength} dBM"}
            Network Type: ${ns.cellType}
            Is Wifi connected: ${ns.isWifiConnected.eng()}
            Is Mobile connected: ${ns.isMobileConnected.eng()}
            Is Emergency Only: ${ns.isEmergencyOnly.eng()}
            Is In Service: ${ns.isInService.eng()}
            Is Out Of Service: ${ns.isOutOfService.eng()}
            Is Powered Off: ${ns.isPowerOff.eng()}
        """.trimIndent()

        binding.deviceDevice.text = """
            Manufacturer: ${ds.manufacturer}
            Model: ${ds.model}
            Brand: ${ds.brand}
            Device: ${ds.device}
            Display: ${ds.display}
            Product: ${ds.product}
            Uptime: ${ds.uptime.toTime()}
        """.trimIndent()

        binding.deviceScreen.text = "${ds.width} x ${ds.height}\n${ds.density} : ${ds.dpWidth} x ${ds.dpHeight}"


        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        deviceSensors.addAll(
                arrayOf(27,
                        Sensor.TYPE_LIGHT,
                        Sensor.TYPE_PROXIMITY,
                        Sensor.TYPE_GRAVITY,
                        Sensor.TYPE_LINEAR_ACCELERATION,
                        Sensor.TYPE_MAGNETIC_FIELD,
                        Sensor.TYPE_GYROSCOPE
                ).map { mSensorManager.getDefaultSensor(it) })
    }


    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            27 -> binding.orientation.text = when (event.values[0].toInt()) {
                0 -> "Normal"
                1 -> "Left"
                2 -> "Upside Down"
                3 -> "Right"
                else -> "Flat | Unknown"
            }
            Sensor.TYPE_LIGHT -> binding.light.text = "Amount of Light: %.0f".format(event.values[0])
            Sensor.TYPE_PROXIMITY -> binding.proximity.text = "%s : %.0f".format(if (event.values[0] > 0) "Away" else "Close", event.values[0])
            Sensor.TYPE_GRAVITY -> binding.gravity.text = "Gravity\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_LINEAR_ACCELERATION -> binding.acceleration.text = "Acceleration\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_MAGNETIC_FIELD -> binding.magnetic.text = "Magnetic\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_GYROSCOPE -> binding.gyroscope.text = "Gyroscope\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    public override fun onResume() {
        super.onResume()
        deviceSensors.forEach { mSensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    public override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
