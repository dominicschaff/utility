@file:Suppress("NOTHING_TO_INLINE")

package zz.utility.maps

import com.google.gson.JsonObject
import org.oscim.core.GeoPoint
import org.oscim.layers.vector.geometries.Style
import zz.utility.R
import zz.utility.helpers.d

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

fun colourStyle(f: String): Style = Style.builder()
        .buffer(0.5)
        .fillColor(f)
        .fillAlpha(0.2F).build()