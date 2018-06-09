package zz.utility

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_wi_fi_scanner.*
import java.util.*

class WiFiScannerActivity : Activity() {

    private lateinit var wifiManager: WifiManager

    private lateinit var br: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wi_fi_scanner)
        wifi.setText(R.string.wait)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(applicationContext, "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show()
            wifiManager.isWifiEnabled = true
        }

        br = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                updateList(wifiManager.scanResults.toTypedArray())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(br)
    }

    private fun updateList(results: Array<ScanResult>) {
        Arrays.sort(results, Comparator { s1, s2 ->
            if (s1.frequency == s2.frequency) {
                return@Comparator if (s1.level == s2.level) s1.SSID.compareTo(s2.SSID) else s1.level - s2.level
            }
            s1.frequency - s2.frequency
        })
        val sb = StringBuilder()
        for ((c, sr) in results.withIndex()) {
            if (c > 0) sb.append("\n\n")
            sb
                    .append(sr.BSSID)
                    .append(" \"").append(sr.SSID).append("\"\n")
                    .append(channel(sr.frequency))
                    .append(":")
                    .append(sr.level)
                    .append(" ")
                    .append(sr.capabilities)

        }
        wifi.text = sb.toString()
    }

    private fun channel(frequency: Int): Int {
        when (frequency) {
            2412 -> return 1
            2417 -> return 2
            2422 -> return 3
            2427 -> return 4
            2432 -> return 5
            2437 -> return 6
            2442 -> return 7
            2447 -> return 8
            2452 -> return 9
            2457 -> return 10
            2462 -> return 11
            2467 -> return 12
            2472 -> return 13
            2484 -> return 14
            else -> return frequency
        }
    }
}
