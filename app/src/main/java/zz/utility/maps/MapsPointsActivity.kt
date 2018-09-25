package zz.utility.maps

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_maps_points.*
import org.oscim.android.canvas.AndroidGraphics.drawableToBitmap
import org.oscim.backend.CanvasAdapter
import org.oscim.core.GeoPoint
import org.oscim.layers.marker.ItemizedLayer
import org.oscim.layers.marker.MarkerItem
import org.oscim.layers.marker.MarkerSymbol
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.MapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.theme.VtmThemes
import org.oscim.tiling.source.mapfile.MapFileTileSource
import zz.utility.HOME
import zz.utility.R
import zz.utility.helpers.*

@SuppressLint("MissingPermission")
class MapsPointsActivity : AppCompatActivity(), ItemizedLayer.OnItemGestureListener<MarkerItem> {
    override fun onItemLongPress(index: Int, item: MarkerItem?): Boolean {
        item ?: return true
        toast("${item.description}: ${item.title} \n${item.geoPoint.longitude}, ${item.geoPoint.latitude}")
        return true
    }

    override fun onItemSingleTapUp(index: Int, item: MarkerItem?): Boolean {
        item ?: return true
        toast(item.title)
        return true
    }

    private lateinit var mapScaleBar: MapScaleBar

    @SuppressLint("MissingPermission")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps_points)
        progress.see()

        // Tile source
        val tileSource = MapFileTileSource()
        if (tileSource.setMapFile("$HOME/area.map")) {
            val tileLayer = mapView.map().setBaseMap(tileSource)
//            mapView.map().layers().add(BuildingLayer(mapView.map(), tileLayer))
            mapView.map().layers().add(LabelLayer(mapView.map(), tileLayer))
            mapView.map().setTheme(VtmThemes.OSMARENDER)

            // Scale bar
            mapScaleBar = DefaultMapScaleBar(mapView.map())
            val mapScaleBarLayer = MapScaleBarLayer(mapView.map(), mapScaleBar)
            mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
            mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
            mapView.map().layers().add(mapScaleBarLayer)
        }

        arrayOf(
                R.drawable.ic_place_green,
                R.drawable.ic_place,
                R.drawable.ic_place_pink,
                R.drawable.ic_place_red
        ).forEachIndexed { index, image ->
            val f = "$HOME/points${index + 1}.json"
            if (f.fileExists()) {
                ItemizedLayer(
                        mapView.map(),
                        ArrayList<MarkerItem>(),
                        MarkerSymbol(drawableToBitmap(getDrawable(image)), MarkerSymbol.HotspotPlace.BOTTOM_CENTER, true),
                        this@MapsPointsActivity
                ).apply {
                    mapView.map().layers().add(this)
                    val pts = f.fileAsJsonArray().mapObject {
                        val lp = LocationPoint(s("name"), d("latitude"), d("longitude"))
                        MarkerItem(lp.name, "${index + 1}", GeoPoint(lp.latitude, lp.longitude))
                    }
                    addItems(pts)
                }
            }
        }

        mapView.map().setMapPosition(-33.0, 18.0, (1 shl 8).toDouble())

        progress.unsee()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapScaleBar.destroy()
        mapView.onDestroy()
        super.onDestroy()
    }

}
