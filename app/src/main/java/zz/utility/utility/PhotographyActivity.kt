package zz.utility.utility

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_photography.*
import org.shredzone.commons.suncalc.*
import zz.utility.R
import zz.utility.helpers.fullDateShortTime
import zz.utility.helpers.now
import zz.utility.helpers.onlyDate
import zz.utility.helpers.shortTime
import java.util.*

class PhotographyActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photography)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locationReceived(location)
    }


    @SuppressLint("SetTextI18n")
    private fun locationReceived(location: Location) {
        val times = SunTimes.compute()
                .on(Date(location.time))
                .at(location.latitude, location.longitude)
                .execute()

        val moon = MoonPosition.compute().execute()
        val moonTimes = MoonTimes.compute().execute()
        val moonIllumination = MoonIllumination.compute().execute()


        val moonNew = MoonPhase.compute().phase(MoonPhase.Phase.NEW_MOON).execute()
        val moonFull = MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON).execute()

        sun_rise.text = dateOrTime(times.rise!!)
        sun_set.text = dateOrTime(times.set!!)
        moon_height.text = "%.0f °".format(moon.altitude)

        moon_rise.text = dateOrTime(moonTimes.rise!!)
        moon_set.text = dateOrTime(moonTimes.set!!)

        moon_next_new.text = moonFull.time.onlyDate()
        moon_next_full.text = moonNew.time.onlyDate()

        moon_percentage_progress.progress = (moonIllumination.fraction * 100).toInt()
        moon_percentage_cycle.progress = moonIllumination.phase.toInt()
    }

    private fun dateOrTime(date: Date): String {
        val now = Date(now())

        return if (now.year == date.year && now.month == date.month && now.date == date.date)
            date.shortTime()
        else if (date.date - 1 == now.date)
            "T " + date.shortTime()
        else date.fullDateShortTime()
    }
}
