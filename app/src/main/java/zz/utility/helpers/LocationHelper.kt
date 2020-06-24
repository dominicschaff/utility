package zz.utility.helpers

fun Float.bearingToCompass(): String = when {
    this < 28 -> "N"
    this < 73 -> "NE"
    this < 118 -> "E"
    this < 163 -> "SE"
    this < 208 -> "S"
    this < 253 -> "SW"
    this < 298 -> "W"
    this < 343 -> "NW"
    else -> "N"
}