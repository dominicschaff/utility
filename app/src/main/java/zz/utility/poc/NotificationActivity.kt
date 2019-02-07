package zz.utility.poc

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_notification.*
import zz.utility.R
import zz.utility.browser.PATH
import zz.utility.browser.gallery.GalleryActivity
import android.graphics.drawable.BitmapDrawable
import android.R.drawable
import androidx.core.graphics.drawable.toBitmap
import zz.utility.MainActivity


const val CHANNEL_ID = "myDefault"

class NotificationActivity : AppCompatActivity() {

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        createNotificationChannel()
        do_send.setOnClickListener {
            val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("My notification")
                    .setContentText("Much longer text that cannot fit one line...")
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line...Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            NotificationManagerCompat.from(this).notify(0, mBuilder.build())
        }
        do_send_image.setOnClickListener {

            val bitmap = getDrawable(R.drawable.ic_place_pink)!!.toBitmap(width=100, height=100)

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("My notification")
                    .setStyle(NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                    )
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setAutoCancel(true)
            NotificationManagerCompat.from(this).notify(0, mBuilder.build())

        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Default channel for this app"
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
