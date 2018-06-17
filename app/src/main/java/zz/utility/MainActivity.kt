package zz.utility

import android.Manifest
import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import zz.utility.books.BooksActivity
import zz.utility.browser.FileBrowserActivity
import zz.utility.helpers.goto
import zz.utility.helpers.requestPermissions
import zz.utility.maps.MapsActivity
import zz.utility.poc.PocMenuActivity
import zz.utility.scrum.ScrumPokerActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goto_quote.setOnClickListener { goto(QuoteActivity::class.java) }
        goto_gps.setOnClickListener { goto(GPSActivity::class.java) }
        goto_sensors.setOnClickListener { goto(SensorsActivity::class.java) }
        goto_wifi.setOnClickListener { goto(WiFiScannerActivity::class.java) }
        goto_random.setOnClickListener { goto(RandomCodeActivity::class.java) }
        goto_qr.setOnClickListener { goto(QRCodeGeneratorActivity::class.java) }
        goto_info.setOnClickListener { goto(InfoActivity::class.java) }
        goto_versions.setOnClickListener { goto(AndroidVersionsActivity::class.java) }
        goto_scan.setOnClickListener { goto(ScanningActivity::class.java) }
        goto_books.setOnClickListener { goto(BooksActivity::class.java) }
        goto_osm_maps.setOnClickListener { goto(MapsActivity::class.java) }
        goto_camera.setOnClickListener { goto(CameraActivity::class.java) }
        goto_scrum_poker.setOnClickListener { goto(ScrumPokerActivity::class.java) }
        goto_tides.setOnClickListener { goto(TidesActivity::class.java) }
        goto_files.setOnClickListener { goto(FileBrowserActivity::class.java) }
        goto_list.setOnClickListener { goto(ListActivity::class.java) }
        goto_poc.setOnClickListener { goto(PocMenuActivity::class.java) }
        goto_images.setOnClickListener { goto(ImageDownloadActivity::class.java) }
        goto_knowledge.setOnClickListener { goto(KnowledgeActivity::class.java) }

        mainGrid.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 4 else 2
        requestPermissions(arrayOf(
                Manifest.permission_group.SMS,
                Manifest.permission_group.CONTACTS,
                Manifest.permission_group.PHONE,
                Manifest.permission_group.LOCATION,
                Manifest.permission_group.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA
        ))
    }
}
