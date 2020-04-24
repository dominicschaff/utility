package zz.utility.utility

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_info.*
import zz.utility.R
import zz.utility.helpers.formatSize
import zz.utility.helpers.stats.getDeviceStats
import zz.utility.helpers.stats.getMemoryStats
import zz.utility.helpers.stats.getNetworkStats
import zz.utility.helpers.stats.getStorageStats
import zz.utility.helpers.toTimeFormat

fun Boolean.eng(): String = if (this) "yes" else "no"

class InfoActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val ss = getStorageStats()
        val ns = getNetworkStats()
        val ms = getMemoryStats()
        val ds = getDeviceStats()

        var tmpStr = "${ss.internal.formatSize()} / ${ss.internalFull.formatSize()}"

        ss.external.indices.forEach {
            tmpStr += "\n${ss.external[it].formatSize()} / ${ss.externalFull[it].formatSize()}"
        }
        device_storage.text = tmpStr

        device_traffic.text = """
            Mobile: ${ns.mobileRx.formatSize()} : ${ns.mobileTx.formatSize()}
            App: ${ns.appRx.formatSize()} : ${ns.appTx.formatSize()}
            Total: ${ns.totalRx.formatSize()} : ${ns.totalTx.formatSize()}
        """.trimIndent()

        device_battery.text = "Level %.1f %%\nTemperature %.1f °".format(ds.battery, ds.battery_temperature)

        device_memory.text = """
            Total:  ${ms.total.formatSize()}
            Available: ${ms.available.formatSize()}
            Threshold: ${ms.threshold.formatSize()}
        """.trimIndent()

        device_network.text = """
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

        device_serials.text = """
            Sim: ${ds.simSerialNumber}
            Device ID: ${ds.deviceIdNumber}
            Device: ${ds.deviceSerial}
        """.trimIndent()

        device_device.text = """
            Manufacturer: ${ds.manufacturer}
            Model: ${ds.model}
            Brand: ${ds.brand}
            Device: ${ds.device}
            Display: ${ds.display}
            Product: ${ds.product}
            Uptime: ${ds.uptime.toTimeFormat()}
        """.trimIndent()

        device_screen.text = "${ds.width} x ${ds.height}\n${ds.density} : ${ds.dpWidth} x ${ds.dpHeight}"
    }


}
