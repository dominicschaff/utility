package zz.utility.utility

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.activity_scanning.*
import zz.utility.R
import zz.utility.helpers.appendToFile
import zz.utility.helpers.fullDate
import java.io.File
import java.util.*

class ScanningActivity : Activity() {

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
        setContentView(R.layout.activity_scanning)
        barcode_scanner.decodeContinuous(callback)
        barcode_content.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val message = lastText ?: return
                if (message.startsWith("WIFI")) {

                    val p1 = message.indexOf(";")
                    val part1 = message.substring(0, p1).split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (part1.size != 3) {
                        Toast.makeText(this@ScanningActivity, "Badly formatted", Toast.LENGTH_LONG).show()
                        return
                    }
                    val wifiType = part1[2]

                    var s = p1 + 3
                    var e = s
                    run {
                        var i = s
                        while (i < message.length) {
                            if (message[i] == '\\') {
                                i++
                                i++
                                continue
                            }
                            if (message[i] == ';') {
                                e = i
                                break
                            }
                            i++
                        }
                    }
                    val ssid = message.substring(s, e).replace(":".toRegex(), ":").replace(";".toRegex(), ";")

                    s = e + 3
                    e = s
                    var i = s
                    while (i < message.length) {
                        if (message[i] == '\\') {
                            i++
                            i++
                            continue
                        }
                        if (message[i] == ';') {
                            e = i
                            break
                        }
                        i++
                    }

                    val password = message.substring(s, e).replace(":".toRegex(), ":").replace(";".toRegex(), ";")
                    Toast.makeText(this@ScanningActivity, "$wifiType-$ssid-$password", Toast.LENGTH_LONG).show()

                    val conf = WifiConfiguration()
                    conf.SSID = "\"" + ssid + "\""
                    when (wifiType) {
                        "WPA", "WPA2" -> conf.preSharedKey = "\"" + password + "\""
                        else -> return
                    }
                    try {
                        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        wifiManager.addNetwork(conf)
                        Toast.makeText(this@ScanningActivity, "Network saved", Toast.LENGTH_LONG).show()
                        val list = wifiManager.configuredNetworks
                        for (wifiItem in list) {
                            if (wifiItem.SSID != null && wifiItem.SSID == "\"" + ssid + "\"") {
                                wifiManager.disconnect()
                                wifiManager.enableNetwork(wifiItem.networkId, true)
                                wifiManager.reconnect()
                                Toast.makeText(this@ScanningActivity, "Network found in saved settings, reconnecting", Toast.LENGTH_LONG).show()
                                return
                            }
                        }
                        Toast.makeText(this@ScanningActivity, "Network not found in saved settings", Toast.LENGTH_LONG).show()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        Toast.makeText(this@ScanningActivity, ex.message, Toast.LENGTH_LONG).show()
                    }

                } else {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(message, message)
                    clipboard.primaryClip = clip
                    Toast.makeText(this@ScanningActivity, "Set clipboard to: $message", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    public override fun onResume() {
        super.onResume()

        barcode_scanner.resume()
    }

    public override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
            barcode_scanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)

    private fun addBarcode(type: String, message: String?) {
        if (message == null) return
        barcodeRead(type, message)
        if (message == lastText) return
        lastText = message

        barcode_content.text = message
        barcode_type.text = type
    }

    private fun barcodeRead(type: String, barcode: String) {
        JsonObject().apply {
            addProperty("event_time", Date().fullDate())
            addProperty("event_type", "barcodeScan")
            addProperty("type", type)
            addProperty("barcode", barcode)
        }.appendToFile(File(Environment.getExternalStorageDirectory(), "log.json"))
    }
}
