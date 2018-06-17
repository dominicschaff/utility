package zz.utility.spy

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.google.gson.JsonObject
import zz.utility.helpers.add
import zz.utility.helpers.appendToFile
import zz.utility.helpers.externalFile
import zz.utility.helpers.fullDate
import java.util.*

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {

        val notification = JsonObject()
                .add("event_time", Date().fullDate())
                .add("event_type", "notification")
                .add("id", sbn.id)
                .add("packageName", sbn.packageName)
                .add("tickerText", sbn.notification.tickerText?.toString() ?: "null")

        val keys = JsonObject()

        sbn.notification.extras.keySet().forEach {
            keys.add(it, sbn.notification.extras.get(it)?.toString() ?: "null")
        }
        notification.add("keys", keys)
        notification.appendToFile("log.json".externalFile())
    }
}
