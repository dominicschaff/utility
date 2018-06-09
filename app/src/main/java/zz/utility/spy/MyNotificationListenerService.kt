package zz.utility.spy

import android.os.Environment
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.google.gson.JsonObject
import zz.utility.helpers.add
import zz.utility.helpers.fullDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val notification = JsonObject()

        notification.add("event_time", Date().fullDate())
        notification.add("event_type", "notification")
        notification.add("id", sbn.id)
        notification.add("packageName", sbn.packageName)
        notification.add("tickerText", sbn.notification.tickerText?.toString() ?: "null")

        val keys = JsonObject()

        for (key in sbn.notification.extras.keySet()) {
            keys.add(key, sbn.notification.extras.get(key)?.toString() ?: "null")
        }
        notification.add("keys", keys)


        val path = Environment.getExternalStorageDirectory()
        val file = File(path, "log.json")
        var stream: FileOutputStream? = null
        try {
            stream = FileOutputStream(file, true)
            stream.write(notification.toString().toByteArray())
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
