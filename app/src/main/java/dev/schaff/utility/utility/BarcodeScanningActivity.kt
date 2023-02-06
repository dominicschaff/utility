package dev.schaff.utility.utility

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dev.schaff.utility.databinding.ActivityScanningBinding
import dev.schaff.utility.helpers.appendToFile
import dev.schaff.utility.helpers.copyToClipboard
import dev.schaff.utility.helpers.toDateFull
import dev.schaff.utility.homeDir
import java.io.File
import java.util.*


class BarcodeScanningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanningBinding

    private var lastText: String? = null

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) return

            addBarcode(result.barcodeFormat.name, result.text)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.barcodeScanner.decodeContinuous(callback)
        binding.barcodeContent.setOnClickListener(View.OnClickListener {
            val message = lastText ?: return@OnClickListener
            copyToClipboard(message)
        })
    }

    public override fun onResume() {
        super.onResume()
        binding.barcodeScanner.resume()
    }

    public override fun onPause() {
        super.onPause()
        binding.barcodeScanner.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
            binding.barcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)

    private fun addBarcode(type: String, message: String) {
        barcodeRead(type, message)
        lastText = message

        binding.barcodeContent.text = message
        binding.barcodeType.text = type
    }

    private fun barcodeRead(type: String, barcode: String) {
        JsonObject().apply {
            addProperty("scan_time", Date().toDateFull())
            addProperty("type", type)
            addProperty("content", barcode)
        }.appendToFile(File(homeDir(), "barcodes.json"))
    }
}
