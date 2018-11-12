package zz.utility.utility

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.graphhopper.GHRequest
import com.graphhopper.GraphHopper
import com.graphhopper.PathWrapper
import com.graphhopper.util.Parameters
import com.graphhopper.util.StopWatch
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_car_dock.*
import org.oscim.android.canvas.AndroidGraphics
import org.oscim.backend.CanvasAdapter
import org.oscim.core.GeoPoint
import org.oscim.core.MapPosition
import org.oscim.layers.LocationLayer
import org.oscim.layers.marker.ItemizedLayer
import org.oscim.layers.marker.MarkerItem
import org.oscim.layers.marker.MarkerSymbol
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
import zz.utility.maps.LocationPoint
import java.io.File
import java.util.*

class CarDockActivity : AppCompatActivity(), LocationListener {

    private lateinit var mapScaleBar: MapScaleBar
    private lateinit var locationLayer: LocationLayer
    private lateinit var locationManager: LocationManager
    private val mapPosition = MapPosition()
    private val locationsSaved = ArrayList<LocationPoint>()

    private lateinit var lastLocation: Location
    private var lastPictureName: String = ""

    private lateinit var mMarkerLayer: ItemizedLayer<MarkerItem>
    private lateinit var pathLayer: PathLayer

    private var currentResponse: PathWrapper? = null

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var photoCapture: Timer

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_dock)

        fotoapparat = Fotoapparat(
                context = this,
                view = camera_view,                   // view which will draw the camera preview
                scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
                lensPosition = back(),               // (optional) we want back camera
                cameraConfiguration = CameraConfiguration(
                        pictureResolution = highestResolution(),
                        previewResolution = lowestResolution(),
                        previewFpsRange = lowestFps(),
                        focusMode = firstAvailable(continuousFocusPicture(), autoFocus(), fixed()),
                        jpegQuality = manualJpegQuality(90)
                )
        )

        // Tile source
        val tileSource = MapFileTileSource()
        if (tileSource.setMapFile("$HOME/area.map")) {
            val tileLayer = mapView.map().setBaseMap(tileSource)
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

        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        mapView.map().setMapPosition(lastLocation.latitude, lastLocation.longitude, (1 shl 12).toDouble())

        onLocationChanged(lastLocation)

        val bitmapPoi = AndroidGraphics.drawableToBitmap(getDrawable(R.drawable.ic_place))

        mMarkerLayer = ItemizedLayer(mapView.map(), ArrayList<MarkerItem>(), MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.CENTER, false), null)
        mapView.map().layers().add(mMarkerLayer)

        val pts = MAIN.fileAsJsonObject().a("locations").mapObject {
            val lp = LocationPoint(s("name"), d("latitude"), d("longitude"))
            locationsSaved.add(lp)
            MarkerItem(lp.name, lp.name, GeoPoint(lp.latitude, lp.longitude))
        }

        mMarkerLayer.addItems(pts)
        setupGraphhopper()

        val style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(0x9900cc33.toInt())
                .strokeWidth(4 * resources.displayMetrics.density)
                .build()
        pathLayer = PathLayer(mapView.map(), style)
        mapView.map().layers().add(pathLayer)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        mapView.onResume()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, this)
        photoCapture = Timer()
        photoCapture.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    lastPictureName = "timelapse_${Date().fileDate()}.jpg"
                    fotoapparat.takePicture().saveToFile(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), lastPictureName))
                    updateInfo()
                }
            }
        }, 3000, 10000)
    }

    override fun onPause() {
        locationManager.removeUpdates(this)
        mapView.onPause()
        super.onPause()
        try {
            photoCapture.cancel()
        } catch (ignored: Exception) {
        }

        try {
            photoCapture.purge()
        } catch (ignored: Exception) {
        }
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

        centerOn(location.latitude, location.longitude)
        mapView.map().updateMap(true)
        updateInfo()
    }

    private fun centerOn(latitude: Double, longitude: Double) {
        mapView.map().getMapPosition(mapPosition)
        mapPosition.setPosition(latitude, longitude)
        mapView.map().mapPosition = mapPosition
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private lateinit var hopper: GraphHopper

    private fun setupGraphhopper() {
        Thread(Runnable {
            {
                val tmpHopp = GraphHopper().forMobile()
                tmpHopp.load("$HOME/area")
                "found graph ${tmpHopp.graphHopperStorage}, nodes:${tmpHopp.graphHopperStorage.nodes}".log()
                hopper = tmpHopp
                runOnUiThread {
                    val titles = Array(locationsSaved.size) { locationsSaved[it].name }
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Select location")
                            .setItems(titles) { _, which ->
                                calcPath(lastLocation.latitude, lastLocation.longitude, locationsSaved[which].latitude, locationsSaved[which].longitude)
                            }
                    builder.show()
                }
            }.orPrint()
        }).start()
    }

    @SuppressLint("StaticFieldLeak")
    private fun calcPath(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double) {
        "calculating path ...".log()
        object : AsyncTask<Void, Void, PathWrapper>() {
            var time: Float = 0.toFloat()

            override fun doInBackground(vararg v: Void): PathWrapper? {
                val sw = StopWatch().start()
                val req = GHRequest(fromLat, fromLon, toLat, toLon).setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
                req.vehicle = "car"
                val resp = hopper.route(req)
                time = sw.stop().seconds
                return try {
                    resp.best
                } catch (e: Exception) {
                    null
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onPostExecute(resp: PathWrapper?) {
                if (resp == null) {
                    toast("Unable to create route")
                    return
                }
                if (!resp.hasErrors()) {
                    currentResponse = resp
                    val t = resp.time / 1000
                    toast("Took ${(time * 1000).toInt()} ms to compute")
                    toast("%d turns, %.1f km (%02d:%02d)".format(
                            resp.instructions.size,
                            resp.distance / 1000.0,
                            t / 60,
                            t % 60))


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

    private fun logUser(str: String) {
        str.log()
        toast(str)
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo() {
        text_data.text = "%.10f, %.10f\nAccuracy: %d\nProvider:%s\n$lastPictureName".format(lastLocation.latitude, lastLocation.longitude, lastLocation.accuracy.toInt(), lastLocation.provider)
    }
}
