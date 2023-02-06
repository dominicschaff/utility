package dev.schaff.utility.helpers

import android.location.Location
import android.location.LocationManager
import com.google.gson.JsonObject
import dev.schaff.utility.R
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

val markerColours = mapOf(
    R.drawable.ic_place_green to "green",
    R.drawable.ic_place_blue to "blue",
    R.drawable.ic_place_pink to "pink",
    R.drawable.ic_place_red to "red",
    R.drawable.ic_place_black to "black",
    R.drawable.ic_place_light_blue to "light_blue",
    R.drawable.ic_place_purple to "purple",
    R.drawable.ic_place_cyan to "cyan"
)

inline fun JsonObject.toGeoPoint() = GeoPoint(d("latitude"), d("longitude"))

data class LocationPoint(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val colour: String = "blue"
)
inline fun LocationPoint.toGeoPoint() = GeoPoint(latitude, longitude)