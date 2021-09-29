package zz.utility.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import zz.utility.databinding.ActivityScanningBinding
import zz.utility.helpers.appendToFile
import zz.utility.helpers.toDateFull
import zz.utility.homeDir
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
            setClipboard(message)
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

    private fun setClipboard(message: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(message, message))
        Toast.makeText(this@BarcodeScanningActivity, "Set clipboard to: $message", Toast.LENGTH_LONG).show()
    }
}
