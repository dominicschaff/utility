package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.JsonObject
import zz.utility.helpers.appendToFile
import zz.utility.helpers.externalFile
import zz.utility.helpers.fullDate
import zz.utility.helpers.orPrint
import java.util.*

class DeviceThingsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        { writeToDisk(intent.action, intent.toUri(Intent.URI_INTENT_SCHEME)) }.orPrint()
    }

    private fun writeToDisk(action: String?, uri: String) {
        JsonObject().apply {
            addProperty("event_time", Date().fullDate())
            addProperty("event_type", "broadcast_receive")
            addProperty("broadcast_type", action ?: "unknown")
            addProperty("broadcast_data", uri)
        }.appendToFile("log.json".externalFile())
    }
}
