package zz.utility.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import kotlinx.android.synthetic.main.activity_gps.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.lib.OpenLocationCode
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

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        if (location.hasAccuracy())
            gps_accuracy.text = "%.0f m".format(location.accuracy)
        if (location.hasSpeed()) {
            gps_speed_m.text = "%.1f".format(location.speed)
            gps_speed_km.text = "%.1f".format(location.speed * 3.6)
        }
        if (location.hasAltitude())
            gps_altitude.text = "%.0f m".format(location.altitude)
        if (location.hasBearing())
            gps_bearing.text = "%.0f°".format(location.bearing)

        gps_lat_long.text = "%.5f %.5f".format(location.latitude, location.longitude)

        val ss = SunriseSunset(location.latitude, location.longitude, Date(location.time), 0.0)

        gps_code.text = OpenLocationCode.encode(location.latitude, location.longitude)

        gps_time_data.text = "${Date(location.time).fullDateDay()}\n${ss.sunrise?.shortTime()} -> ${ss.sunset?.shortTime()}"

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
