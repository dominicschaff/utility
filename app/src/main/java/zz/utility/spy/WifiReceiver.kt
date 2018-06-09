package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Environment
import android.telephony.TelephonyManager
import com.google.gson.JsonObject
import zz.utility.helpers.add
import zz.utility.helpers.fullDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class WifiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = conMan.activeNetworkInfo
        var carrierName = ""
        var simOperatorName = ""
        try {
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                if (manager != null) {
                    carrierName = manager.networkOperatorName
                    simOperatorName = manager.simOperatorName
                }
            }
        } catch (ignored: Exception) {
        }


        val json = JsonObject()
                .add("event_time", Date().fullDate())
                .add("event_type", "network_change")
                .add("type", activeNetwork.typeName)
                .add("subType", activeNetwork.subtypeName)
                .add("carrierName", carrierName)
                .add("simOperatorName", simOperatorName)
                .add("state", activeNetwork.state.name)
                .add("detailedState", activeNetwork.detailedState.name)
                .add("reason", activeNetwork.reason ?: "none")
                .add("extra", activeNetwork.extraInfo ?: "none")
                .add("roaming", activeNetwork.isRoaming)
                .add("available", activeNetwork.isAvailable)
                .add("failover", activeNetwork.isFailover)

        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "log.json")
        var stream: FileOutputStream? = null
        try {
            stream = FileOutputStream(file, true)
            stream.write(json.toString().toByteArray())
            stream.write("\n".toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (stream != null) stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}
