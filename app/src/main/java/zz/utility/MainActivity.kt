package zz.utility

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import zz.utility.demo.LoginActivity
import zz.utility.helpers.goto
import zz.utility.helpers.requestPermissions
import zz.utility.helpers.toast
import zz.utility.maps.MapsActivity
import zz.utility.text.TextActivity
import zz.utility.utility.BarcodeScanningActivity
import zz.utility.utility.InfoActivity
import zz.utility.utility.QRCodeGeneratorActivity
import zz.utility.utility.data.ItemListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goto_qr.setOnClickListener { goto(QRCodeGeneratorActivity::class.java) }
        goto_info.setOnClickListener { goto(InfoActivity::class.java) }
        goto_scan.setOnClickListener { goto(BarcodeScanningActivity::class.java) }
        goto_osm_maps.setOnClickListener { goto(MapsActivity::class.java) }
        goto_data.setOnClickListener { goto(ItemListActivity::class.java) }

        goto_test_login.setOnClickListener { goto(LoginActivity::class.java) }
        goto_shared.setOnClickListener { goto(TextActivity::class.java) }

        if (!requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                ))) {
            toast("Something went wrong")
        }
    }
}
