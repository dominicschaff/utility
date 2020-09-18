@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.helpers

import java.text.SimpleDateFormat
import java.util.*

val fullDate = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
val fullDateShortTime = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
val onlyDate = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
val shortTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
val fullTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
val longDateTime = SimpleDateFormat("yyyy-MM-dd EEE HH:mm:ss", Locale.ENGLISH)
val fileDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH)

inline fun Date.toDateFull(): String = fullDate.format(this)
inline fun Date.toDateShortTime(): String = fullDateShortTime.format(this)
inline fun Date.toDateDay(): String = longDateTime.format(this)
inline fun Date.toTimeShort(): String = shortTime.format(this)
inline fun Date.toTimeFull(): String = fullTime.format(this)
inline fun Date.toDateFile(): String = fileDate.format(this)
inline fun Date.toDate(): String = onlyDate.format(this)
inline fun Long.toTime(): String {
    var time = this
    val milli = time % 1000
    time /= 1000
    val seconds = time % 60
    time /= 60
    val minutes = time % 60
    val hours = time / 60
    return "%02d:%02d:%02d.%d".format(hours, minutes, seconds, milli)
}

inline fun Long.toTimeShort(): String {
    var time = this
    time /= 1000
    time /= 60
    val minutes = time % 60
    val hours = time / 60
    return "%02d:%02d".format(hours, minutes)
}

inline fun now(): Long = System.currentTimeMillis()

fun Date?.addHours(hours: Int): Date? {
    this ?: return null
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.HOUR_OF_DAY, hours)
    return cal.time
}