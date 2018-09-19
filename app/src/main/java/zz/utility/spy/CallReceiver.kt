package zz.utility.spy

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonObject
import zz.utility.helpers.appendToFile
import zz.utility.helpers.externalFile
import zz.utility.helpers.fullDate
import java.util.*

class CallReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        writeToDisk(intent.extras)
    }

    private fun writeToDisk(bundle: Bundle?) {
        JsonObject().apply {
            addProperty("event_time", Date().fullDate())
            addProperty("event_type", "call")
            bundle!!.keySet().forEach {
                addProperty(it, bundle.get(it)?.toString() ?: "null")
            }
        }.appendToFile("log.json".externalFile())
    }
}
