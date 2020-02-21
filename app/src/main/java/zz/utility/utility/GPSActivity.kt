package zz.utility.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_gps.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.lib.OpenLocationCode
import zz.utility.lib.SunriseSunset
import java.util.*

class GPSActivity : Activity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private var record = false

    override fun onCreate(savedInstanceState: Bundle?) {
        {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_gps)
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            fab_save.setOnClickListener {
                record = true
                fab_save.hide()
            }
        }.orPrint()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        progress.hide()
        if (location.hasAccuracy())
            gps_accuracy.text = "%.0f m".format(location.accuracy)

        if (location.hasSpeed()) {
            gps_speed_m.text = "%.1f".format(location.speed)
            gps_speed_km.text = "%.1f".format(location.speed * 3.6)
        }

        if (location.hasAltitude())
            gps_altitude.text = "%.0f m".format(location.altitude)

        if (location.hasBearing())
            gps_bearing.text = "%s %.0f°".format(location.bearing.bearingToCompass(), location.bearing)

        gps_lat_long.text = "%.5f %.5f".format(location.latitude, location.longitude)

        val ss = SunriseSunset(location.latitude, location.longitude, Date(location.time), 0.0)

        gps_code.text = OpenLocationCode.encode(location.latitude, location.longitude)

        gps_time_data.text = "${Date(location.time).fullDateDay()}\n${ss.sunrise?.shortTime()} -> ${ss.sunset?.shortTime()}"
        if (record) {
            JsonObject().apply {
                addProperty("event_time", Date().fullDate())
                addProperty("latitude", location.latitude)
                addProperty("longitude", location.longitude)
                addProperty("accuracy", location.accuracy)
                addProperty("speed", location.speed)

                addProperty("altitude", location.altitude)

                addProperty("bearing", location.bearing)
                addProperty("provider", location.provider)
                addProperty("bearingAccuracyDegrees", location.bearingAccuracyDegrees)
                addProperty("speedAccuracyMetersPerSecond", location.speedAccuracyMetersPerSecond)
                addProperty("verticalAccuracyMeters", location.verticalAccuracyMeters)
                addProperty("openLocationCode", OpenLocationCode.encode(location.latitude, location.longitude))
                addProperty("time", Date(location.time).fullDateDay())
                addProperty("sunrise", ss.sunrise?.fullDateDay())
                addProperty("sunset", ss.sunset?.fullDateDay())
            }.appendToFile("utility/location.json".externalFile())
        }
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        if (hasLocationPermissions()) toast("We need GPS settings to make this screen work")
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            locationManager.registerGnssStatusCallback(object: GnssStatus.Callback() {
                @SuppressLint("SetTextI18n")
                override fun onSatelliteStatusChanged(status: GnssStatus?) {
                    super.onSatelliteStatusChanged(status)
                    status?:return

                    val usedInFix = (0 until status.satelliteCount).map { if (status.usedInFix(it)) 1 else 0 }.sum()
                    gps_status.text = "$usedInFix / ${status.satelliteCount} satelites"
                }
            })
        }
        super.onResume()
    }

    public override fun onPause() {
        if (hasLocationPermissions()) toast("We need GPS settings to make this screen work")
        else
            locationManager.removeUpdates(this)
        super.onPause()
    }

    public override fun onStop() {
        if (hasLocationPermissions()) toast("We need GPS settings to make this screen work")
        else
            locationManager.removeUpdates(this)
        super.onStop()
    }
}
