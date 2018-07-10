package zz.utility.spy

import android.os.Build
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

        if (sbn.packageName in arrayOf("com.internet.speed.meter"))
            return

        val notification = JsonObject()
                .add("event_time", Date().fullDate())
                .add("event_type", "notification")
                .add("id", sbn.id)
                .add("packageName", sbn.packageName ?: "unknown")
                .add("tickerText", sbn.notification.tickerText?.toString() ?: "null")
                .add("groupKey", sbn.groupKey ?: "null")
                .add("isClearable", sbn.isClearable)
                .add("postTime", Date(sbn.postTime).fullDate())
                .add("isGroup", sbn.isGroup)
                .add("isOngoing", sbn.isOngoing)
                .add("tag", sbn.tag ?: "null")
                .add("category", sbn.notification.category ?: "null")
                .add("color", sbn.notification.color)
                .add("group", sbn.notification.group ?: "null")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.add("timeoutAfter", sbn.notification.timeoutAfter)
            notification.add("settingsText", sbn.notification.settingsText?.toString() ?: "null")
        }
        val keys = JsonObject()

        sbn.notification?.extras?.keySet()?.forEach {
            keys.add(it, sbn.notification.extras.get(it)?.toString() ?: "null")
        }
        notification.add("keys", keys)
        notification.appendToFile("log.json".externalFile())
    }
}
