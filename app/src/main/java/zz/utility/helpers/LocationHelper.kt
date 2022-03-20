package zz.utility.helpers

import android.location.Location
import android.location.LocationManager
import org.oscim.core.GeoPoint
import org.oscim.layers.vector.geometries.Style

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

fun Location.distanceTo(point: GeoPoint): Float =
    this.distanceTo(Location(LocationManager.GPS_PROVIDER).apply {
        this.latitude = point.latitude
        this.longitude = point.longitude
    })

fun colourStyle(f: String): Style = Style.builder()
    .buffer(0.5)
    .fillColor(f)
    .fillAlpha(0.2F).build()