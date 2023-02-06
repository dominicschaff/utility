package dev.schaff.utility

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import dev.schaff.utility.databinding.ActivityMainBinding
import dev.schaff.utility.utility.*
import dev.schaff.utility.demo.LoginActivity
import dev.schaff.utility.helpers.goto
import dev.schaff.utility.helpers.requestPermissions
import dev.schaff.utility.helpers.toast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gotoOsmMaps.setOnClickListener { goto(MapActivity::class.java) }
        binding.gotoQr.setOnClickListener { goto(QRCodeGeneratorActivity::class.java) }
        binding.gotoInfo.setOnClickListener { goto(InfoActivity::class.java) }
        binding.gotoScan.setOnClickListener { goto(BarcodeScanningActivity::class.java) }
        binding.gotoNotes.setOnClickListener { goto(NotesActivity::class.java) }
        binding.gotoClock.setOnClickListener { goto(ClockActivity::class.java) }
        binding.gotoDownload.setOnClickListener { goto(LocalFileActivity::class.java) }
        binding.gotoCalculate.setOnClickListener { goto(CounterActivity::class.java) }

        binding.gotoTestLogin.setOnClickListener { goto(LoginActivity::class.java) }
        binding.gotoSize.setOnClickListener { goto(SizeActivity::class.java) }

        if (!requestPermissions(arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.POST_NOTIFICATIONS
                ))) {
            toast("Something went wrong")
        }
        if (!Environment.isExternalStorageManager()) {
            startActivity(
                Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            )
        }
    }
}
