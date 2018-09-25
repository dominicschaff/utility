package zz.utility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.view.*
import zz.utility.browser.FileBrowserActivity
import zz.utility.helpers.goto
import zz.utility.maps.MapsActivity
import zz.utility.maps.MapsPointsActivity
import zz.utility.poc.PocMenuActivity
import zz.utility.scrum.ScrumPokerActivity
import zz.utility.utility.*

class UtilityFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.activity_main, null).apply {
            goto_quote.setOnClickListener { activity?.goto(QuoteActivity::class.java) }
            goto_gps.setOnClickListener { activity?.goto(GPSActivity::class.java) }
            goto_sensors.setOnClickListener { activity?.goto(SensorsActivity::class.java) }
            goto_wifi.setOnClickListener { activity?.goto(WiFiScannerActivity::class.java) }
            goto_qr.setOnClickListener { activity?.goto(QRCodeGeneratorActivity::class.java) }
            goto_info.setOnClickListener { activity?.goto(InfoActivity::class.java) }
            goto_versions.setOnClickListener { activity?.goto(AndroidVersionsActivity::class.java) }
            goto_scan.setOnClickListener { activity?.goto(ScanningActivity::class.java) }
            goto_osm_maps.setOnClickListener { activity?.goto(MapsActivity::class.java) }
            goto_camera.setOnClickListener { activity?.goto(CameraScalesActivity::class.java) }
            goto_scrum_poker.setOnClickListener { activity?.goto(ScrumPokerActivity::class.java) }
            goto_files.setOnClickListener { activity?.goto(FileBrowserActivity::class.java) }
            goto_list.setOnClickListener { activity?.goto(ListActivity::class.java) }
            goto_poc.setOnClickListener { activity?.goto(PocMenuActivity::class.java) }
            goto_images.setOnClickListener { activity?.goto(ImageDownloadActivity::class.java) }
            goto_knowledge.setOnClickListener { activity?.goto(KnowledgeActivity::class.java) }
            goto_car_dock.setOnClickListener { activity?.goto(CarDockActivity::class.java) }
            goto_daily_comic.setOnClickListener { activity?.goto(DailyComicActivity::class.java) }
            goto_quote_api.setOnClickListener { activity?.goto(QuoteApiActivity::class.java) }
            goto_draw.setOnClickListener { activity?.goto(TouchScreenActivity::class.java) }
            goto_map_points.setOnClickListener { activity?.goto(MapsPointsActivity::class.java) }

            mainGrid.columnCount = if (this@UtilityFragment.resources.getBoolean(R.bool.is_landscape)) 4 else 2
        }
    }
}