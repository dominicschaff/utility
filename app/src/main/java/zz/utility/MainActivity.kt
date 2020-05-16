package zz.utility

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import zz.utility.browser.FileBrowserActivity
import zz.utility.demo.LoginActivity
import zz.utility.helpers.consume
import zz.utility.helpers.goto
import zz.utility.helpers.requestPermissions
import zz.utility.helpers.toast
import zz.utility.maps.MapsActivity
import zz.utility.utility.*
import zz.utility.utility.data.ItemListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goto_sensors.setOnClickListener { goto(SensorsActivity::class.java) }
        goto_qr.setOnClickListener { goto(QRCodeGeneratorActivity::class.java) }
        goto_info.setOnClickListener { goto(InfoActivity::class.java) }
        goto_scan.setOnClickListener { goto(BarcodeScanningActivity::class.java) }
        goto_osm_maps.setOnClickListener { goto(MapsActivity::class.java) }
        goto_files.setOnClickListener { goto(FileBrowserActivity::class.java) }
        goto_list.setOnClickListener { goto(ListActivity::class.java) }
        goto_images.setOnClickListener { goto(ImageDownloadActivity::class.java) }
        goto_daily_comic.setOnClickListener { goto(DailyComicActivity::class.java) }
        goto_draw.setOnClickListener { goto(TouchScreenActivity::class.java) }
        goto_dev_rant.setOnClickListener { goto(DevRantActivity::class.java) }
        goto_moon.setOnClickListener { goto(PhotographyActivity::class.java) }
        goto_data.setOnClickListener { goto(ItemListActivity::class.java) }

        goto_test_login.setOnClickListener { goto(LoginActivity::class.java) }

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
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.READ_CONTACTS
                ))) {
            toast("Something went wrong")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean = consume { menuInflater.inflate(R.menu.menu_main, menu) }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> consume {
            goto(SettingsActivity::class.java)
        }
        else -> super.onOptionsItemSelected(item)
    }
}
