package zz.utility.utility

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_info.*
import zz.utility.R
import zz.utility.helpers.formatSize
import zz.utility.helpers.stats.getDeviceStats
import zz.utility.helpers.stats.getMemoryStats
import zz.utility.helpers.stats.getNetworkStats
import zz.utility.helpers.stats.getStorageStats
import zz.utility.helpers.toTimeFormat

fun Boolean.eng(): String = if (this) "yes" else "no"

class InfoActivity : Activity() {
    private fun LinearLayout.addThing(title: String, content: String) {
        val cv = layoutInflater.inflate(R.layout.card_view, this, false)
        cv.findViewById<TextView>(R.id.heading).text = title
        cv.findViewById<TextView>(R.id.content).text = content
        addView(cv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val ss = getStorageStats()
        val ns = getNetworkStats()
        val ms = getMemoryStats()
        val ds = getDeviceStats()

        with(listy) {
            addThing("Sim Serial", ds.simSerialNumber)
            addThing("Device ID", ds.deviceIdNumber)

            // Get all the info:
            addThing("Internal Memory", "${ss.internal.formatSize()} / ${ss.internalFull.formatSize()}")

            ss.external.indices.forEach {
                addThing("External Memory", "${ss.external[it].formatSize()} / ${ss.externalFull[it].formatSize()}")
            }

            addThing("Mobile RX", ns.mobileRx.formatSize())
            addThing("Mobile TX", ns.mobileTx.formatSize())
            addThing("Total RX", ns.totalRx.formatSize())
            addThing("Total TX", ns.totalTx.formatSize())
            addThing("App RX", ns.appRx.formatSize())
            addThing("App TX", ns.appTx.formatSize())

            addThing("Device Serial", ds.deviceSerial)
            addThing("Battery Level", "%.1f".format(ds.battery))
            addThing("Battery Temperature", "%.1f".format(ds.battery_temperature))
            addThing("Manufacturer", ds.manufacturer)
            addThing("Model", ds.model)
            addThing("Brand", ds.brand)
            addThing("Device", ds.device)
            addThing("Display", ds.display)
            addThing("Product", ds.product)

            addThing("Total Memory", ms.total.formatSize())
            addThing("Available Memory", ms.available.formatSize())
            addThing("Threshold Memory", ms.threshold.formatSize())

            addThing("Screen Size", "${ds.width} x ${ds.height}")
            addThing("Screen Density", "${ds.density} : ${ds.dpWidth} x ${ds.dpHeight}")
            addThing("Uptime", ds.uptime.toTimeFormat())
            addThing("Operator Name", ns.operatorName)
            addThing("Network State", ns.serviceStateDescription)
            addThing("Network Strength", "${ns.signalStrength} dBM")
            addThing("Network Type", ns.cellType)
            addThing("Is Wifi connected", ns.isWifiConnected.eng())
            addThing("Is Mobile connected", ns.isMobileConnected.eng())
            addThing("Is Emergency Only", ns.isEmergencyOnly.eng())
            addThing("Is In Service", ns.isInService.eng())
            addThing("Is Out Of Service", ns.isOutOfService.eng())
            addThing("Is Powered Off", ns.isPowerOff.eng())
        }
    }


}
