package zz.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.CardView
import android.widget.LinearLayout
import android.widget.TextView
import zz.utility.helpers.formatSize
import zz.utility.helpers.toTimeFormat
import kotlinx.android.synthetic.main.activity_info.*
import zz.utility.lib.OpenLocationCode
import zz.utility.helpers.stats.DeviceStats
import zz.utility.helpers.stats.MemoryStats
import zz.utility.helpers.stats.NetworkStats
import zz.utility.helpers.stats.StorageStats

class InfoActivity : Activity(), LocationListener {


    @Suppress("NOTHING_TO_INLINE")
    private inline fun LinearLayout.addThing(title: String, content: String) {
        val cv = layoutInflater.inflate(R.layout.card_view, this, false) as CardView
        cv.findViewById<TextView>(R.id.heading).text = title
        cv.findViewById<TextView>(R.id.content).text = content
        this.addView(cv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        refresh()
    }

    private fun refresh() {
        listy.removeAllViews()

        val ss = StorageStats.get(this)
        val ns = NetworkStats.get(this)
        val ms = MemoryStats.get(this)
        val ds = DeviceStats.get(this)

        listy.addThing("Sim Serial", ds.simSerialNumber)
        listy.addThing("Device ID", ds.deviceIdNumber)

        // Get all the info:
        listy.addThing("Internal Memory", ss.internal.formatSize() + " / " + ss.internal.formatSize())

        for (i in ss.external.indices) {
            listy.addThing("External Memory", ss.external[i].formatSize() + " / " + ss.externalFull[i].formatSize())
        }

        listy.addThing("Mobile RX", ns.mobileRx.formatSize())
        listy.addThing("Mobile TX", ns.mobileTx.formatSize())
        listy.addThing("Total RX", ns.totalRx.formatSize())
        listy.addThing("Total TX", ns.totalTx.formatSize())
        listy.addThing("App RX", ns.appRx.formatSize())
        listy.addThing("App TX", ns.appTx.formatSize())

        listy.addThing("Device Serial", ds.deviceSerial)
        listy.addThing("Battery Level", "%.1f".format(ds.battery))
        listy.addThing("Battery Temperature", "%.1f".format(ds.battery_temperature))
        listy.addThing("Manufacturer", ds.manufacturer)
        listy.addThing("Model", ds.model)
        listy.addThing("Brand", ds.brand)
        listy.addThing("Device", ds.device)
        listy.addThing("Display", ds.display)
        listy.addThing("Product", ds.product)

        listy.addThing("Total Memory", ms.total.formatSize())
        listy.addThing("Available Memory", ms.available.formatSize())
        listy.addThing("Threshold Memory", ms.threshold.formatSize())

        listy.addThing("Screen Size", "%dx%d".format(ds.width, ds.height))
        listy.addThing("Uptime", ds.uptime.toTimeFormat())
        listy.addThing("Operator Name", ns.operatorName)
        listy.addThing("Network State", ns.serviceStateDescription)
        listy.addThing("Network Strength", "%d dBM".format(ns.signalStrength))
        listy.addThing("Network Type", ns.cellType)
        listy.addThing("Is Wifi connected", if (ns.isWifiConnected) "yes" else "no")
        listy.addThing("Is Mobile connected", if (ns.isMobileConnected) "yes" else "no")

        val cv = layoutInflater.inflate(R.layout.card_view, listy, false) as CardView
        val tvh = cv.findViewById<TextView>(R.id.heading)
        tvh.text = "Current Location"
        location = cv.findViewById<TextView>(R.id.content)
        location.text = "---"
        listy.addView(cv)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
    }

    private lateinit var location: TextView

    override fun onPause() {
        super.onPause()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        val code = OpenLocationCode.encode(location.latitude, location.longitude, 11)
        this.location.text = "%.7f,%.7f = %s".format(location.latitude, location.longitude, code)

    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

}
