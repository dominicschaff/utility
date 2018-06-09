@file:Suppress("NOTHING_TO_INLINE", "unused")

package zz.utility.helpers

import java.text.SimpleDateFormat
import java.util.*

val fullDate = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
val shortDate = SimpleDateFormat("dd MMM", Locale.ENGLISH)
val shortTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
val onlyDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
val fullTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
val longDateTime = SimpleDateFormat("HH:mm:ss, EEE, yyyy-MM-dd", Locale.ENGLISH)
val fileDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH)

inline fun Date.fullDate(): String = fullDate.format(this)
inline fun Date.fullDateDay(): String = longDateTime.format(this)
inline fun Date.timeOnly(): String = shortTime.format(this)
inline fun Date.fullTime(): String = fullTime.format(this)
inline fun Date.dateOnly(): String = onlyDate.format(this)
inline fun Date.shortDate(): String = shortDate.format(this)
inline fun Date.fileDate(): String = fileDate.format(this)
inline fun Long.toTimeFormat(): String {
    var time = this
    val milli = time % 1000
    time /= 1000
    val seconds = time % 60
    time /= 60
    val minutes = time % 60
    val hours = time / 60
    return "%02d:%02d:%02d.%d".format(hours, minutes, seconds, milli)
}

inline fun now(): Long = System.currentTimeMillis()

fun startOfTheDay(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}