package zz.utility.spy

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.google.gson.JsonObject
import zz.utility.helpers.appendToFile
import zz.utility.helpers.externalFile
import zz.utility.helpers.fullDate
import java.util.*

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        if (sbn.packageName in arrayOf("com.internet.speed.meter"))
            return

        val notification = JsonObject().apply {
            addProperty("event_time", Date().fullDate())
            addProperty("event_type", "notification")
            addProperty("id", sbn.id)
            addProperty("packageName", sbn.packageName ?: "unknown")
            addProperty("tickerText", sbn.notification.tickerText?.toString() ?: "null")
            addProperty("groupKey", sbn.groupKey ?: "null")
            addProperty("isClearable", sbn.isClearable)
            addProperty("postTime", Date(sbn.postTime).fullDate())
            addProperty("isGroup", sbn.isGroup)
            addProperty("isOngoing", sbn.isOngoing)
            addProperty("tag", sbn.tag ?: "null")
            addProperty("category", sbn.notification.category ?: "null")
            addProperty("color", sbn.notification.color)
            addProperty("group", sbn.notification.group ?: "null")


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                addProperty("timeoutAfter", sbn.notification.timeoutAfter)
                addProperty("settingsText", sbn.notification.settingsText?.toString() ?: "null")
            }
        }
        val keys = JsonObject().apply {
            sbn.notification?.extras?.keySet()?.forEach {
                addProperty(it, sbn.notification.extras.get(it)?.toString() ?: "null")
            }
        }
        notification.add("keys", keys)
        notification.appendToFile("log.json".externalFile())
    }
}
