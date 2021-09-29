package zz.utility.demo

import android.accounts.AccountManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import zz.utility.MainActivity
import zz.utility.R
import zz.utility.databinding.ActivityTestScreenBinding
import zz.utility.helpers.error
import zz.utility.helpers.toDateFull
import java.util.*

const val CHANNEL_ID = "myDefault"

class TestScreenActivity : AppCompatActivity() {

    private lateinit var tts: TextToSpeech
    private lateinit var binding: ActivityTestScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        createNotificationChannel()
        binding.doSend.setOnClickListener {
            val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            NotificationManagerCompat.from(this).notify(0, mBuilder.build())
        }
        binding.doSendImage.setOnClickListener {

            val bitmap = ContextCompat.getDrawable(this, R.drawable.ic_place_pink)!!
                .toBitmap(width = 100, height = 100)

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("My notification")
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                )
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true)
            NotificationManagerCompat.from(this).notify(0, mBuilder.build())

        }

        doBotStart()
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "My Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Default channel for this app"
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun doBotStart() {
        add("Initializing system...")
        add("Local time: ${Date().toDateFull()}")
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.UK)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    "This Language is not supported".error("TTS")
                }
                add("Speech enabled")
                speak("Hello")

            } else {
                "Initilization Failed!".error("TTS")
                add("Voice cannot be used")
            }
            doContinue()
        }
    }

    public override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }


    private fun doContinue() {
        add("Searching calendar")
        add("Searching local events")
        add("Searching Accounts...")
        getAccounts()
        add("Searching Contacts...")
    }

    private fun getAccounts() {

        val manager = AccountManager.get(this)
        val accounts = manager.accounts
//        val accounts = manager.getAccountsByType("com.google")

        accounts.forEach { add("Found account: ${it.type}\n\t\t${it.name}") }
    }

    private fun add(s: String) = binding.mainText.append("$s\n")
    private fun speak(text: String) = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
}
