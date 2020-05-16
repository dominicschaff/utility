package zz.utility.utility

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_photography.*
import org.shredzone.commons.suncalc.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.lib.SunriseSunset
import java.util.*


fun Date.zodiacSign(): String {
    val month = this.month + 1
    val day = this.date
    return when {
        (month == 12 && day >= 22 && day <= 31) || (month == 1 && day >= 1 && day <= 19) -> "Capricorn"
        (month == 1 && day >= 20 && day <= 31) || (month == 2 && day >= 1 && day <= 17) -> "Aquarius"
        (month == 2 && day >= 18 && day <= 29) || (month == 3 && day >= 1 && day <= 19) -> "Pisces"
        (month == 3 && day >= 20 && day <= 31) || (month == 4 && day >= 1 && day <= 19) -> "Aries"
        (month == 4 && day >= 20 && day <= 30) || (month == 5 && day >= 1 && day <= 20) -> "Taurus"
        (month == 5 && day >= 21 && day <= 31) || (month == 6 && day >= 1 && day <= 20) -> "Gemini"
        (month == 6 && day >= 21 && day <= 30) || (month == 7 && day >= 1 && day <= 22) -> "Cancer"
        (month == 7 && day >= 23 && day <= 31) || (month == 8 && day >= 1 && day <= 22) -> "Leo"
        (month == 8 && day >= 23 && day <= 31) || (month == 9 && day >= 1 && day <= 22) -> "Virgo"
        (month == 9 && day >= 23 && day <= 30) || (month == 10 && day >= 1 && day <= 22) -> "Libra"
        (month == 10 && day >= 23 && day <= 31) || (month == 11 && day >= 1 && day <= 21) -> "Scorpio"
        (month == 11 && day >= 22 && day <= 30) || (month == 12 && day >= 1 && day <= 21) -> "Sagittarius"
        else -> "Illegal date"
    }
}

class PhotographyActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photography)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    @SuppressLint("SetTextI18n")
    private fun locationReceived(location: Location) {
        progress.hide()
        val times = SunTimes.compute()
                .on(Date(location.time))
                .at(location.latitude, location.longitude)
                .execute()

        val sun = SunPosition.compute().on(Date(location.time)).at(location.latitude, location.longitude).execute()

        val moon = MoonPosition.compute().execute()
        val moonTimes = MoonTimes.compute().execute()
        val moonIllumination = MoonIllumination.compute().execute()


        val moonNew = MoonPhase.compute().phase(MoonPhase.Phase.NEW_MOON).execute()
        val moonFull = MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON).execute()

        sun_rise.text = dateOrTime(times.rise)
        sun_rise_golden.text = "${dateOrTime(times.rise)} - ${dateOrTime(times.rise.addHours(1))}"
        sun_rise_blue.text = "${dateOrTime(times.rise.addHours(-1))} - ${dateOrTime(times.rise)}"
        sun_rise_twilight.text = "${dateOrTime(times.rise.addHours(-2))} - ${dateOrTime(times.rise.addHours(-1))}"

        sun_set_golden.text = "${dateOrTime(times.set.addHours(-1))} - ${dateOrTime(times.set)}"
        sun_set_blue.text = "${dateOrTime(times.set)} - ${dateOrTime(times.set.addHours(1))}"
        sun_set_twilight.text = "${dateOrTime(times.set.addHours(1))} - ${dateOrTime(times.set.addHours(2))}"

        sun_set.text = dateOrTime(times.set!!)

        sun_noon.text = "Next Noon: ${dateOrTime(times.noon)}"
        sun_alitude.text = "%.0f °".format(sun.altitude)

        moon_height.text = "%.0f °".format(moon.altitude)

        moon_rise.text = dateOrTime(moonTimes.rise)
        moon_set.text = dateOrTime(moonTimes.set)

        moon_next_new.text = moonNew.time.toDate()
        moon_next_full.text = moonFull.time.toDate()

        moon_percentage_progress.text = "${(moonIllumination.fraction * 100).toInt()} %"
        moon_percentage_cycle.text = "${(moonIllumination.phase / 180 * 100).toInt()}"

        val month = Date().month
        date_season.text = if (location.latitude < 0) when {
            month < 2 || month == 11 -> "Summer"
            month < 5 -> "Autumn"
            month < 8 -> "Winter"
            else -> "Spring"
        } else when {
            month < 2 || month == 11 -> "Winter"
            month < 5 -> "Spring"
            month < 8 -> "Summer"
            else -> "Autumn"
        }

        date_star_sign.text = Date().zodiacSign()

        data.text = """
            %.5f, %.5f
            ${Date(location.time).toDateFull()}
            Accuracy: ${location.accuracy.toInt()} m | Altitude: ${location.altitude.toInt()} m
        """.trimIndent().format(location.latitude, location.longitude)

        val ss = SunriseSunset(location.latitude, location.longitude, Date(location.time), 0.0)
        sun_day_length.text = (ss.sunset!!.time - ss.sunrise!!.time).toTimeShort()
        sun_night_length.text = (24 * 60 * 60 * 1000 - (ss.sunset!!.time - ss.sunrise!!.time)).toTimeShort()
    }

    private fun dateOrTime(date: Date?): String {
        date ?: return "---"
        val now = Date(now())

        return if (now.year == date.year && now.month == date.month && now.date == date.date)
            date.toTimeShort()
        else if (date.date - 1 == now.date)
            "T " + date.toTimeShort()
        else date.toDateShortTime()
    }

    override fun onLocationChanged(location: Location?) {
        location ?: return
        locationReceived(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }
}
