package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.google.gson.JsonObject
import zz.utility.helpers.fullDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        writeToDisk(intent.extras)
    }

    private fun writeToDisk(bundle: Bundle?) {
        val sms = JsonObject()

        sms.addProperty("event_time", Date().fullDate())
        sms.addProperty("event_type", "call")
        for (s in bundle!!.keySet()) {
            val a = bundle.get(s)
            sms.addProperty(s, a?.toString() ?: "null")
        }
        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "log.json")
        var stream: FileOutputStream? = null
        try {
            stream = FileOutputStream(file, true)
            stream.write(sms.toString().toByteArray())
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
