package zz.utility.spy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import zz.utility.helpers.fullDate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.experimental.and


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            Log.e("check", "received")
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    val number = smsMessage.originatingAddress
                    val messageBody = smsMessage.messageBody
                    val pdu = try {
                        bytesToHex(smsMessage.pdu)
                    } catch (e: Exception) {
                        e.message
                    }
                    val userData = try {
                        bytesToHex(smsMessage.userData)
                    } catch(e:Exception) {
                        e.message
                    }
                    writeToDisk(number, messageBody, pdu.orEmpty(), userData.orEmpty())
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun writeToDisk(from: String, msg: String, pdu: String, userData: String) {
        val sms = JsonObject()

        sms.addProperty("event_time", Date().fullDate())
        sms.addProperty("event_type", "message")
        sms.addProperty("from", from)
        sms.addProperty("message", msg)
        sms.addProperty("pdu", pdu)
        sms.addProperty("userData", userData)
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

    companion object {

        private val hexArray = "0123456789ABCDEF".toCharArray()

        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v: Int = (bytes[j] and 0xFF.toByte()).toInt()
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }
}
