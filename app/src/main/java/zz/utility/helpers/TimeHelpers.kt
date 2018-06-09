@file:Suppress("NOTHING_TO_INLINE", "unused")

package zz.utility.helpers

inline fun Int.seconds(): Long = this * 1000L
inline fun Int.minutes(): Long = this * 60000L
inline fun Int.hours(): Long = this * 3600000L
inline fun Int.days(): Long = this * 86400000L