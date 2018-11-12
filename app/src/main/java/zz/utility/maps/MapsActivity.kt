package zz.utility.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.graphhopper.GHRequest
import com.graphhopper.GraphHopper
import com.graphhopper.PathWrapper
import com.graphhopper.util.Parameters
import com.graphhopper.util.StopWatch
import kotlinx.android.synthetic.main.activity_maps.*
import org.oscim.android.canvas.AndroidGraphics.drawableToBitmap
import org.oscim.backend.CanvasAdapter
import org.oscim.core.GeoPoint
import org.oscim.core.MapPosition
import org.oscim.event.Gesture
import org.oscim.event.GestureListener
import org.oscim.event.MotionEvent
import org.oscim.layers.Layer
import org.oscim.layers.LocationLayer
import org.oscim.layers.marker.ItemizedLayer
import org.oscim.layers.marker.MarkerItem
import org.oscim.layers.marker.MarkerSymbol
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.layers.vector.PathLayer
import org.oscim.layers.vector.geometries.Style
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.MapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.theme.VtmThemes
import org.oscim.tiling.source.mapfile.MapFileTileSource
import zz.utility.HOME
import zz.utility.MAIN
import zz.utility.R
import zz.utility.helpers.*

data class LocationPoint(
        val name: String,
        val latitude: Double,
        val longitude: Double
)

@SuppressLint("MissingPermission")
class MapsActivity : AppCompatActivity(), LocationListener, ItemizedLayer.OnItemGestureListener<MarkerItem> {

    private lateinit var mapScaleBar: MapScaleBar
    private lateinit var locationLayer: LocationLayer
    private lateinit var locationManager: LocationManager
    private val mapPosition = MapPosition()
    private var followMe = false
    private val locationsSaved = ArrayList<LocationPoint>()

    private var useCar = true
    private var rotateFollow = false

    private lateinit var lastLocation: Location

    private lateinit var mMarkerLayer: ItemizedLayer<MarkerItem>
    private lateinit var pathLayer: PathLayer

    private var currentResponse: PathWrapper? = null

    @SuppressLint("MissingPermission")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        progress.see()

        // Tile source
        val tileSource = MapFileTileSource()
        if (tileSource.setMapFile("$HOME/area.map")) {
            val tileLayer = mapView.map().setBaseMap(tileSource)
            mapView.map().layers().add(BuildingLayer(mapView.map(), tileLayer))
            mapView.map().layers().add(LabelLayer(mapView.map(), tileLayer))
            mapView.map().setTheme(VtmThemes.OSMARENDER)

            // Scale bar
            mapScaleBar = DefaultMapScaleBar(mapView.map())
            val mapScaleBarLayer = MapScaleBarLayer(mapView.map(), mapScaleBar)
            mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
            mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
            mapView.map().layers().add(mapScaleBarLayer)
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationLayer = LocationLayer(mapView.map())
        locationLayer.locationRenderer.setShader("location_1_reverse")
        locationLayer.isEnabled = false
        mapView.map().layers().add(locationLayer)

        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER))

        mapView.map().setMapPosition(lastLocation.latitude, lastLocation.longitude, (1 shl 12).toDouble())

        onLocationChanged(lastLocation)

        val bitmapPoi = drawableToBitmap(getDrawable(R.drawable.ic_place))

        mMarkerLayer = ItemizedLayer(mapView.map(), ArrayList<MarkerItem>(), MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.BOTTOM_CENTER, true), this)
        mapView.map().layers().add(mMarkerLayer)

        val pts = MAIN.fileAsJsonObject().a("locations").mapObject {
            val lp = LocationPoint(s("name"), d("latitude"), d("longitude"))
            locationsSaved.add(lp)
            MarkerItem(lp.name, lp.name, GeoPoint(lp.latitude, lp.longitude))
        }

        mMarkerLayer.addItems(pts)
        setupGraphhopper()

        navigate.setOnClickListener {
            val titles = Array(locationsSaved.size) { locationsSaved[it].name }
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select location")
                    .setItems(titles) { _, which ->
                        calcPath(lastLocation.latitude, lastLocation.longitude, locationsSaved[which].latitude, locationsSaved[which].longitude)
                    }
            builder.show()
        }
        center_on_me.setOnClickListener {
            followMe = !followMe
            if (!rotateFollow)
                rotateFollow = true
            onLocationChanged(lastLocation)
        }

        share.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)

            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Shared Location")
            i.putExtra(Intent.EXTRA_TEXT, "http://maps.google.com/maps?q=loc:%.10f,%.10f".format(lastLocation.latitude, lastLocation.longitude))

            try {
                startActivity(Intent.createChooser(i, "Share Location"))
            } catch (ex: android.content.ActivityNotFoundException) {
                toast("There is no activity to share location to.")
            }
        }

        val style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(ContextCompat.getColor(this, R.color.colorAccent))
                .strokeWidth(4 * resources.displayMetrics.density)
                .build()
        pathLayer = PathLayer(mapView.map(), style)
        mapView.map().layers().add(pathLayer)

        mapView.map().layers().add(object : Layer(mapView.map()), GestureListener {
            override fun onGesture(g: Gesture?, e: MotionEvent?): Boolean {
                g ?: return false
                e ?: return false
                return when (g) {
                    is Gesture.Tap -> consume {
                        val p = mMap.viewport().fromScreenPoint(e.x, e.y)
                        toast("You clicked on ${p.latitude}, ${p.longitude}")
                    }
                    is Gesture.LongPress -> consume {
                        val p = mMap.viewport().fromScreenPoint(e.x, e.y)
                        toast("Navigating to ${p.latitude}, ${p.longitude}", Toast.LENGTH_SHORT)
                        calcPath(lastLocation.latitude, lastLocation.longitude, p.latitude, p.longitude)
                    }
                    else -> false
                }
            }

        })

        vehicle.setOnClickListener {
            vehicle.setImageDrawable(getDrawable(if (useCar) R.drawable.map_walk else R.drawable.map_car))
            useCar = !useCar
        }

        center.setOnClickListener {
            mapView.map().viewport().setRotation(0.0)
            rotateFollow = false
        }

        val intent = intent ?: return
        val data = intent.data ?: return
        Thread {
            Thread.sleep(2000)
            runOnUiThread {
                val path = data.pathSegments
                when (path.size) {
                    0 -> {
                    }
                    1 -> {
                        val point = locationsSaved.find { it.name.toLowerCase().contains(path[0].toLowerCase()) }
                                ?: return@runOnUiThread
                        calcPath(lastLocation.latitude, lastLocation.longitude, point.latitude, point.longitude)
                        centerOn(point.latitude, point.longitude)
                    }
                    2 -> {
                        try {
                            val latitude = path[0].toDouble()
                            val longitude = path[1].toDouble()
                            centerOn(latitude, longitude)
                            calcPath(lastLocation.latitude, lastLocation.longitude, latitude, longitude)
                        } catch (e: Exception) {
                            toast("Couldn't decode: ${data.path}")
                        }
                    }
                    else -> toast("Unable to understand given app link")
                }
            }
        }.start()


    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        mapView.onResume()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, this)
    }

    override fun onPause() {
        locationManager.removeUpdates(this)
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapScaleBar.destroy()
        mapView.onDestroy()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        lastLocation = location
        locationLayer.isEnabled = true
        locationLayer.setPosition(location.latitude, location.longitude, location.accuracy.toDouble())

        // Follow location
        if (followMe) centerOn(location.latitude, location.longitude)
        if (rotateFollow) {
            mapView.map().viewport().setRotation(location.bearing.toDouble() * -1.0)
            mapView.map().viewport().setTilt(60F)
        }
        mapView.map().updateMap(true)
        gps_data.text = "%.8f, %.8f (%d:%s)".format(location.latitude, location.longitude, location.accuracy.toInt(), location.provider)
    }

    private fun centerOn(latitude: Double, longitude: Double) {
        mapView.map().getMapPosition(mapPosition)
        mapPosition.setPosition(latitude, longitude)
//        mapPosition.setScale(120000.0)
        mapView.map().mapPosition = mapPosition
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onItemSingleTapUp(index: Int, item: MarkerItem?): Boolean {
        item ?: return true
        toast("Is here: " + item.getTitle())
        return true
    }

    override fun onItemLongPress(index: Int, item: MarkerItem?): Boolean {
        item ?: return true
        toast("Navigating to:" + item.getTitle())
        calcPath(lastLocation.latitude, lastLocation.longitude, item.geoPoint.latitude, item.geoPoint.longitude)

        return true
    }


    private lateinit var hopper: GraphHopper


    @SuppressLint("StaticFieldLeak")
    private fun setupGraphhopper() {
        Thread(Runnable {
            {
                val tmpHopp = GraphHopper().forMobile()
                tmpHopp.load("$HOME/area")
                log("found graph " + tmpHopp.graphHopperStorage.toString() + ", nodes:" + tmpHopp.graphHopperStorage.nodes)
                hopper = tmpHopp
            }.orPrint()
            runOnUiThread { progress.unsee() }
        }).start()
    }

    @SuppressLint("StaticFieldLeak")
    private fun calcPath(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double) {
        progress.see()
        object : AsyncTask<Void, Void, PathWrapper>() {
            var time: Float = 0.toFloat()

            override fun doInBackground(vararg v: Void): PathWrapper? {
                val sw = StopWatch().start()
                val req = GHRequest(fromLat, fromLon, toLat, toLon).setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
                req.vehicle = if (useCar) "car" else "foot"
                val resp = hopper.route(req)
                time = sw.stop().seconds
                return { resp.best }.or { null }
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(resp: PathWrapper?) {
                progress.unsee()
                if (resp == null) {
                    toast("Unable to create route")
                    return
                }
                if (!resp.hasErrors()) {
                    currentResponse = resp
                    val t = resp.time / 1000
                    path.text = "%d turns, %.1f km (%02d:%02d)".format(
                            resp.instructions.size,
                            resp.distance / 1000.0,
                            t / 60,
                            t % 60)
                    path.see()
                    toast("Took ${(time * 1000).toInt()} ms to compute")


                    val geoPoints = ArrayList<GeoPoint>()
                    val pointList = resp.points

                    for (i in 0 until pointList.size)
                        geoPoints.add(GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)))
                    pathLayer.setPoints(geoPoints)

                    mapView.map().updateMap(true)

                } else {
                    logUser("Error:" + resp.errors)
                }
            }
        }.execute()
    }

    private fun log(str: String) {
        Log.i("GH", str)
    }

    private fun logUser(str: String) {
        log(str)
        toast(str)
    }

}
