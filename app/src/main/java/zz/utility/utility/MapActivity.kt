package zz.utility.utility

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
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
import org.oscim.layers.marker.MarkerInterface
import org.oscim.layers.marker.MarkerItem
import org.oscim.layers.marker.MarkerSymbol
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.layers.vector.VectorLayer
import org.oscim.layers.vector.geometries.CircleDrawable
import org.oscim.map.Viewport
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.MapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.theme.VtmThemes
import org.oscim.tiling.source.mapfile.MapFileTileSource
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource
import zz.utility.R
import zz.utility.configFile
import zz.utility.databinding.ActivityMapBinding
import zz.utility.helpers.*
import zz.utility.homeDir
import zz.utility.views.chooser
import java.io.File
import java.util.*


@SuppressLint("MissingPermission")
class MapActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityMapBinding

    private lateinit var mapScaleBar: MapScaleBar
    private lateinit var locationLayer: LocationLayer
    private lateinit var vectorLayer: VectorLayer
    private lateinit var locationManager: LocationManager
    private val mapPosition = MapPosition()
    private var followMe = false
    private val locationsSaved = ArrayList<LocationPoint>()

    private var rotateFollow = false

    private lateinit var lastLocation: Location

    private var daylight = true

    @SuppressLint("MissingPermission")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.gpsDataInfo) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        // Tile source
        val multiTileSource = MultiMapFileTileSource()

        val world = File(homeDir(), "world.map")
        if (world.exists() && world.isFile) {
            val worldTileSource = MapFileTileSource()
            worldTileSource.setMapFile(world.absolutePath)
            multiTileSource.add(worldTileSource, Viewport.MIN_ZOOM_LEVEL, 9)
        }

        homeDir().listFiles()?.forEach {
            if (it.isFile && it.name.endsWith(".map") && it.name != "world.map") {
                val tileSource = MapFileTileSource()
                tileSource.setMapFile(it.absolutePath)
                multiTileSource.add(tileSource)
            }
        }

        val tileLayer = binding.mapView.map().setBaseMap(multiTileSource)
        binding.mapView.map().layers().add(BuildingLayer(binding.mapView.map(), tileLayer))
        binding.mapView.map().layers().add(LabelLayer(binding.mapView.map(), tileLayer))
        binding.mapView.map().setTheme(VtmThemes.OSMARENDER)
        vectorLayer = VectorLayer(binding.mapView.map())
        binding.mapView.map().layers().add(vectorLayer)

        // Scale bar
        mapScaleBar = DefaultMapScaleBar(binding.mapView.map())
        val mapScaleBarLayer = MapScaleBarLayer(binding.mapView.map(), mapScaleBar)
        mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
        mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
        binding.mapView.map().layers().add(mapScaleBarLayer)

        binding.fabTheme.setOnClickListener {
            binding.mapView.map()
                .setTheme(if (daylight) VtmThemes.NEWTRON else VtmThemes.OSMARENDER)
            daylight = !daylight
        }

        binding.centerOnMe.setOnClickListener {
            if (followMe or rotateFollow) {
                followMe = false
                rotateFollow = false
            }
            binding.mapView.map().viewport().setRotation(0.0)
            binding.mapView.map().viewport().setTilt(0F)
            binding.mapView.map().viewport().setMapViewCenter(0f, 0f)
            centerOn(lastLocation.latitude, lastLocation.longitude)
        }

        binding.share.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)

            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Shared Location")
            i.putExtra(
                Intent.EXTRA_TEXT,
                "http://maps.google.com/maps?q=loc:%.10f,%.10f".format(
                    lastLocation.latitude,
                    lastLocation.longitude
                )
            )

            try {
                startActivity(Intent.createChooser(i, "Share Location"))
            } catch (ex: ActivityNotFoundException) {
                toast("There is no activity to share location to.")
            }
        }

        binding.follow.setOnClickListener {
            binding.mapView.map().viewport().setRotation(0.0)
            binding.mapView.map().viewport().setMapViewCenter(0f, 0.5f)
            binding.mapView.map().viewport().setTilt(60F)
            rotateFollow = true
            followMe = true
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationLayer = LocationLayer(binding.mapView.map())
        locationLayer.isEnabled = false
        binding.mapView.map().layers().add(locationLayer)

        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    ?: Location("tmp").apply {
                latitude = -32.0
                longitude = 18.0
            }

        binding.mapView.map()
            .setMapPosition(lastLocation.latitude, lastLocation.longitude, (1 shl 12).toDouble())

        onLocationChanged(lastLocation);
        {
            locationsSaved.addAll(
                configFile().a("locations").mapObject {
                    LocationPoint(
                        s("name"),
                        d("latitude"),
                        d("longitude"),
                        s("colour", "blue")
                    )
                })
        }.ignore()

        binding.mapView.map().layers().add(object : Layer(binding.mapView.map()), GestureListener {
            override fun onGesture(g: Gesture?, e: MotionEvent?): Boolean {
                g ?: return false
                e ?: return false
                return when (g) {
                    is Gesture.Tap -> consume {
                        val p = mMap.viewport().fromScreenPoint(e.x, e.y)
                        val d = mMap.viewport().fromScreenPoint(e.x, e.y).vincentyDistance(
                            GeoPoint(
                                lastLocation.latitude,
                                lastLocation.longitude
                            )
                        )
                        if (d > 2000) {
                            toast(
                                "You clicked on %.6f, %.6f (%.2f km from you)".format(
                                    p.latitude,
                                    p.longitude,
                                    d / 1000
                                )
                            )
                        } else
                            toast("You clicked on ${p.latitude}, ${p.longitude} (${d.toInt()} m from you)")
                    }
                    is Gesture.LongPress -> consume {
                        val p = mMap.viewport().fromScreenPoint(e.x, e.y)
                        chooser(
                            "Select Action",
                            resources.getStringArray(R.array.map_actions),
                            callback = { index, _ ->
                                when (index) {
                                    0 -> {

                                        val i = Intent(Intent.ACTION_SEND)

                                        i.type = "text/plain"
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Shared Location")
                                        i.putExtra(
                                            Intent.EXTRA_TEXT,
                                            "http://maps.google.com/maps?q=loc:%.10f,%.10f".format(
                                                p.latitude,
                                                p.longitude
                                            )
                                        )

                                        try {
                                            startActivity(Intent.createChooser(i, "Share Location"))
                                        } catch (ex: ActivityNotFoundException) {
                                            toast("There is no activity to share location to.")
                                        }
                                    }
                                    1 -> {
                                        toast("Distance to there is: ${lastLocation.distanceTo(p) / 1000} km")
                                    }
                                    2 -> {
                                        request(
                                            "Enter distance",
                                            InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                                        ) { input ->
                                            {
                                                vectorLayer.add(
                                                    CircleDrawable(
                                                        p,
                                                        input.toDouble(),
                                                        colourStyle("#3C92CA")
                                                    )
                                                )
                                                binding.mapView.map().updateMap(true)
                                            }.orPrint()
                                        }
                                    }
                                }
                            })
                    }
                    else -> false
                }
            }
        })

        markerColours.forEach { (image, name) ->
            val items = ArrayList<MarkerItem>()
            items.addAll(locationsSaved.filter { it.colour == name }.map {
                MarkerItem(it.name, it.name, it.toGeoPoint())
            })
            ItemizedLayer(
                binding.mapView.map(),
                items as List<MarkerInterface>,
                MarkerSymbol(
                    drawableToBitmap(AppCompatResources.getDrawable(this, image)),
                    MarkerSymbol.HotspotPlace.BOTTOM_CENTER,
                    true
                ),
                object : ItemizedLayer.OnItemGestureListener<MarkerInterface> {
                    override fun onItemSingleTapUp(index: Int, item: MarkerInterface?): Boolean {
                        item ?: return true
                        toast("This is: " + items[index].title)
                        return true
                    }

                    override fun onItemLongPress(index: Int, item: MarkerInterface?): Boolean {
                        return false
                    }
                }
            ).apply {
                binding.mapView.map().layers().add(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, this)
    }

    override fun onPause() {
        locationManager.removeUpdates(this)
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapScaleBar.destroy()
        try {
            binding.mapView.onDestroy()
        } catch (err: NullPointerException) {

        }

        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        lastLocation = location
        locationLayer.isEnabled = true
        locationLayer.setPosition(location.latitude, location.longitude, location.accuracy)
        binding.locationType.text = location.provider

        // Follow location
        if (followMe) centerOn(location.latitude, location.longitude)
        if (rotateFollow && location.speed > 1.0) {
            binding.mapView.map().viewport().setRotation(location.bearing.toDouble() * -1.0)
        }
        binding.mapView.map().updateMap(true)
        if (location.hasAccuracy())
            binding.gpsAccuracy.text = "%.0f".format(location.accuracy)

        if (location.hasSpeed()) {
            binding.gpsSpeedMS.text = "%.1f".format(location.speed)
            binding.gpsSpeed.text = "%.1f".format(location.speed * 3.6)
        }

        if (location.hasAltitude())
            binding.gpsAltitude.text = "%.0f".format(location.altitude)

        if (location.hasBearing()) {
            binding.gpsBearingValue.text = "%.0f°".format(location.bearing)
            binding.gpsBearing.text = location.bearing.bearingToCompass()
        }

        binding.gpsLatLong.text = "%.5f %.5f".format(location.latitude, location.longitude)

        val ss = SunriseSunset(location.latitude, location.longitude, Date(location.time), 0.0)

        binding.gpsCode.text = OpenLocationCode(location.latitude, location.longitude).code

        binding.gpsTimeData.text =
            "${Date(location.time).toTimeFull()}\n${ss.sunrise?.toTimeShort()} -> ${ss.sunset?.toTimeShort()}"
    }

    private fun centerOn(latitude: Double, longitude: Double) {
        binding.mapView.map().getMapPosition(mapPosition)
        mapPosition.setPosition(latitude, longitude)
//        mapPosition.setScale(120000.0)
        binding.mapView.map().mapPosition = mapPosition
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}
