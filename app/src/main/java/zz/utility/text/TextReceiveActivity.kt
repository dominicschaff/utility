package zz.utility.text

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.JsonObject
import zz.utility.helpers.*
import java.util.*
import kotlin.math.min


class TextReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get intent, action and MIME type
        val intent = intent
        val action = intent.action
        val type: String? = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSendText(intent) // Handle text being sent
            }
        } else {
            longToast("Didn't know what to do exiting...")
        }
        finish()
    }

    private fun handleSendText(intent: Intent) {
        val sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            JsonObject().apply {
                addProperty("event_time", Date().fullDate())
                addProperty("event_type", "sharedMessage")
                addProperty("text", sharedText)
            }.appendToFile("utility/shared.json".externalFile())
            shortToast("Received and saved text: ${sharedText.substring(0, min(sharedText.length, 200))}")
        }

    }
}
