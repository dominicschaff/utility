package zz.utility.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.NavUtils
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_gps.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.lib.SunriseSunset
import java.util.*

class GPSActivity : Activity(), LocationListener {

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_gps)
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }.orPrint()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private var previousLocation: Location? = null

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        if (location.hasAccuracy())
            gps_accuracy.text = "%3.1f m".format(location.accuracy)
        if (location.hasSpeed())
            gps_speed.text = "%.1f m/s<-> %.1f km/h".format(location.speed, location.speed * 3.6)
        if (location.hasAltitude())
            gps_altitude.text = "%3.1f m".format(location.altitude)
        if (location.hasBearing())
            gps_bearing.text = "${location.bearing}"

        if (previousLocation != null) {
            gps_acceleration.text = "%.2f  km/h/s".format((location.speed * 3.6 - previousLocation!!.speed * 3.6) / ((location.time - previousLocation!!.time) / 1000))
        }
        previousLocation = location


        gps_lat_long.text = "%.8f, %.8f".format(location.latitude, location.longitude)

        val ss = SunriseSunset(location.latitude, location.longitude, Date(location.time), 0.0)

        gps_time_data.text = "${Date(location.time).fullDateDay()}\n${ss.sunrise?.fullTime()} -> ${ss.sunset?.fullTime()}"

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
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
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
