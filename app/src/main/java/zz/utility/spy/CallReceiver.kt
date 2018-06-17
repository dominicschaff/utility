package zz.utility.spy

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonObject
import zz.utility.helpers.add
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
        val sms = JsonObject()
                .add("event_time", Date().fullDate())
                .add("event_type", "call")
        bundle!!.keySet().forEach {
            val a = bundle.get(it)
            sms.add(it, a?.toString() ?: "null")
        }
        sms.appendToFile("log.json".externalFile())
    }
}
