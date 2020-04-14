package zz.utility

import android.Manifest
import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import zz.utility.browser.FileBrowserActivity
import zz.utility.helpers.goto
import zz.utility.helpers.requestPermissions
import zz.utility.helpers.toast
import zz.utility.maps.MapsActivity
import zz.utility.maps.MapsPointsActivity
import zz.utility.poc.PocMenuActivity
import zz.utility.utility.*
import zz.utility.utility.data.ItemListActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goto_gps.setOnClickListener { goto(GPSActivity::class.java) }
        goto_sensors.setOnClickListener { goto(SensorsActivity::class.java) }
        goto_qr.setOnClickListener { goto(QRCodeGeneratorActivity::class.java) }
        goto_info.setOnClickListener { goto(InfoActivity::class.java) }
        goto_scan.setOnClickListener { goto(BarcodeScanningActivity::class.java) }
        goto_osm_maps.setOnClickListener { goto(MapsActivity::class.java) }
        goto_files.setOnClickListener { goto(FileBrowserActivity::class.java) }
        goto_list.setOnClickListener { goto(ListActivity::class.java) }
        goto_poc.setOnClickListener { goto(PocMenuActivity::class.java) }
        goto_images.setOnClickListener { goto(ImageDownloadActivity::class.java) }
        goto_daily_comic.setOnClickListener { goto(DailyComicActivity::class.java) }
        goto_quote_api.setOnClickListener { goto(QuoteApiActivity::class.java) }
        goto_draw.setOnClickListener { goto(TouchScreenActivity::class.java) }
        goto_map_points.setOnClickListener { goto(MapsPointsActivity::class.java) }
        goto_dev_rant.setOnClickListener { goto(DevRantActivity::class.java) }
        goto_moon.setOnClickListener { goto(PhotographyActivity::class.java) }
        goto_data.setOnClickListener { goto(ItemListActivity::class.java) }

        if (!requestPermissions(arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE
                ))) {
            toast("Something went wrong")
        }
    }
}
