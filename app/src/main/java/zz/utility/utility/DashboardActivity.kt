package zz.utility.utility

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import zz.utility.R
import zz.utility.helpers.*
import java.util.*


class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        getEvents()
    }

    private fun getCalendars() {
        val cr = contentResolver

        val mProjection = arrayOf(
                CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_LOCATION,
                CalendarContract.Calendars.CALENDAR_TIME_ZONE
        )

        val uri = CalendarContract.Calendars.CONTENT_URI

        val cur = cr.query(uri, mProjection, null, null, null)

        while (cur!!.moveToNext()) {
            val displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
            val accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME))


            val tv1 = TextView(this)
            tv1.text = "DisplayName: $displayName\nAccountName: $accountName"
            listy.addView(tv1)
        }
        cur.close()
    }

    @SuppressLint("SetTextI18n")
    private fun getEvents() {
        val cr = contentResolver

        val mProjection = arrayOf(
                "_id",
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME
        )

        val uri = CalendarContract.Events.CONTENT_URI
        val selection = "(${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTEND} <= ?)"
        val selectionArgs = arrayOf(now().toString(), (now() + 30.days()).toString())

        val cur = cr.query(uri, mProjection, selection, selectionArgs, "${CalendarContract.Events.DTSTART} ASC, ${CalendarContract.Events.ALL_DAY} DESC")

        while (cur!!.moveToNext()) {
            val title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE))
            val allDay = cur.getInt(cur.getColumnIndex(CalendarContract.Events.ALL_DAY)) == 1
            val start = Date(cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTSTART)))
            val end = Date(cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTEND)))
            val cal = cur.getString(cur.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME))

            val tv1 = TextView(this)
            tv1.text = "$title [${showTimes(allDay, onlyDayCalendars(cal), start, end)}]"
            listy.addView(tv1)
        }

    }

    private fun showTimes(allDay: Boolean, showSingleDate: Boolean, start: Date, end: Date): String {
        return if (allDay) {
            if (showSingleDate) start.onlyDate()
            else "${start.onlyDate()} - ${end.onlyDate()}"
        } else {
            val sDate = start.onlyDate()
            val eDate = end.onlyDate()
            if (sDate == eDate) {
                "$sDate ${start.shortTime()} - ${end.shortTime()}"
            } else {
                "${start.fullDate()} - ${end.fullDate()}"
            }
        }
    }

    private fun onlyDayCalendars(name: String) = name.startsWith("Holidays in")
}
