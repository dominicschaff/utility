package zz.utility.text

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import zz.utility.externalFile
import zz.utility.helpers.appendToFile
import zz.utility.helpers.toDateFull
import zz.utility.helpers.toast
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
            toast("Didn't know what to do exiting...")
        }
        finish()
    }

    private fun handleSendText(intent: Intent) {
        val sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            TextDB(this).add(Content(0, Category(0, "Default", 0), sharedText))
            JsonObject().apply {
                addProperty("event_time", Date().toDateFull())
                addProperty("event_type", "sharedMessage")
                addProperty("text", sharedText)
            }.appendToFile(externalFile("shared.json"))
            toast("Received and saved text: ${sharedText.substring(0, min(sharedText.length, 200))}", Toast.LENGTH_SHORT)
        }

    }
}
