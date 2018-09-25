package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import zz.utility.helpers.*
import java.util.*


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        {
            Log.e("check", "received")
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    val number = smsMessage.originatingAddress ?: "unknown"
                    val messageBody = smsMessage.messageBody
                    val pdu = { smsMessage.pdu.toHex() }.orMessage()
                    val userData = { smsMessage.userData.toHex() }.orMessage()
                    writeToDisk(number, messageBody, pdu, userData)
                }
            }
        }.or { Toast.makeText(context, it.message, Toast.LENGTH_LONG).show() }
    }

    private fun writeToDisk(from: String, msg: String, pdu: String, userData: String) {
        JsonObject().apply {
            addProperty("event_time", Date().fullDate())
            addProperty("event_type", "message")
            addProperty("message", msg)
            addProperty("from", from)
            addProperty("pdu", pdu)
            addProperty("userData", userData)
        }.appendToFile("log.json".externalFile())
    }
}
