package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import com.google.gson.JsonObject
import zz.utility.helpers.fullDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DeviceThingsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, intent.action, Toast.LENGTH_LONG).show()
        try {
            writeToDisk(intent.action, intent.toUri(Intent.URI_INTENT_SCHEME))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeToDisk(action: String?, uri: String) {
        val sms = JsonObject()

        sms.addProperty("event_time", Date().fullDate())
        sms.addProperty("event_type", "broadcast_receive")
        sms.addProperty("broadcast_type", action)
        sms.addProperty("broadcast_data", uri)
        val file = File(Environment.getExternalStorageDirectory(), "log.json")
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
