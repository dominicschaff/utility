@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.helpers

import java.text.SimpleDateFormat
import java.util.*

val fullDate = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
val fullTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
val shortTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
val longDateTime = SimpleDateFormat("yyyy-MM-dd EEE HH:mm:ss", Locale.ENGLISH)
val fileDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH)

inline fun Date.fullDate(): String = fullDate.format(this)
inline fun Date.fullDateDay(): String = longDateTime.format(this)
inline fun Date.fullTime(): String = fullTime.format(this)
inline fun Date.shortTime(): String = shortTime.format(this)
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