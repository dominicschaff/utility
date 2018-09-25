package zz.utility.poc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.activity_pos.*
import zz.utility.R

class POSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pos)

        barcode_scanner.decodeContinuous(callback)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) = processScan(result.barcodeFormat.name, result.text)

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) = Unit
    }

    override fun onResume() {
        super.onResume()
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    @SuppressLint("SetTextI18n")
    fun processScan(type: String, code: String) {
        scanned_barcode.text = """$type : $code"""
    }
}
