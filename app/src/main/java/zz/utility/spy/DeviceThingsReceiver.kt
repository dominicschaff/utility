package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.JsonObject
import zz.utility.helpers.*
import java.util.*

class DeviceThingsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        { writeToDisk(intent.action, intent.toUri(Intent.URI_INTENT_SCHEME)) }.orPrint()
    }

    private fun writeToDisk(action: String?, uri: String) {
        JsonObject()
                .add("event_time", Date().fullDate())
                .add("event_type", "broadcast_receive")
                .add("broadcast_type", action ?: "unknown")
                .add("broadcast_data", uri)
                .appendToFile("log.json".externalFile())
    }
}
