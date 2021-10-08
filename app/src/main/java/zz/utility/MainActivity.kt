package zz.utility

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zz.utility.databinding.ActivityMainBinding
import zz.utility.demo.LoginActivity
import zz.utility.helpers.goto
import zz.utility.helpers.requestPermissions
import zz.utility.helpers.toast
import zz.utility.maps.MapsActivity
import zz.utility.utility.*
import zz.utility.utility.data.ItemListActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gotoQr.setOnClickListener { goto(QRCodeGeneratorActivity::class.java) }
        binding.gotoInfo.setOnClickListener { goto(InfoActivity::class.java) }
        binding.gotoScan.setOnClickListener { goto(BarcodeScanningActivity::class.java) }
        binding.gotoOsmMaps.setOnClickListener { goto(MapsActivity::class.java) }
        binding.gotoData.setOnClickListener { goto(ItemListActivity::class.java) }
        binding.gotoClock.setOnClickListener { goto(ClockActivity::class.java) }
        binding.gotoDownload.setOnClickListener { goto(LocalFileActivity::class.java) }
        binding.gotoCalculate.setOnClickListener { goto(CounterActivity::class.java) }

        binding.gotoTestLogin.setOnClickListener { goto(LoginActivity::class.java) }

        if (!requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE
                ))) {
            toast("Something went wrong")
        }
    }
}
