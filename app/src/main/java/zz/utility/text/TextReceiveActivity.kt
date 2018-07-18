package zz.utility.text

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
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
            } else if (type.startsWith("image/")) {
                handleSendImage(intent) // Handle single image being sent
            }
        } else {
            longToast("Didn't know what to do exiting...")
        }
        finish()
    }

    private fun handleSendText(intent: Intent) {
        val sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            JsonObject()
                    .add("event_time", Date().fullDate())
                    .add("event_type", "sharedMessage")
                    .add("text", sharedText)
                    .appendToFile("utility/shared.json".externalFile())
            shortToast("Received and saved text: ${sharedText.substring(0, min(sharedText.length, 200))}")
        }
    }

    private fun handleSendImage(intent: Intent) {
        val imageUri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
        if (imageUri != null) {

            val mimeType = contentResolver.getType(imageUri).split("/")[1]

//            val type = intent.type.split("/")[1]
//            val sourceFilename = imageUri.path
            val destinationFilename = "utility/${Date().fileDate()}.$mimeType".externalFile()
//
//            var bis: BufferedInputStream? = null
//            var bos: BufferedOutputStream? = null
//
//            try {
//                bis = BufferedInputStream(FileInputStream(sourceFilename))
//                bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
//                val buf = ByteArray(1024)
//                bis.read(buf)
//                do {
//                    bos.write(buf)
//                } while (bis.read(buf) != -1)
//                longToast("Received image saved to: ${destinationFilename.absolutePath}")
//            } catch (e: IOException) {
//                longToast(e.message.toString())
//            } finally {
//                try {
//                    bis?.close()
//                    bos?.close()
//                } catch (e: IOException) {
//                    longToast(e.message.toString())
//                }
//
//            }
        }
    }
}
