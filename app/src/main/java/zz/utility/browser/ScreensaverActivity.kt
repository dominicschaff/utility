package zz.utility.browser

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_screensaver.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.isImage
import java.io.File
import java.util.*
import kotlin.collections.random


class ScreensaverActivity : FullscreenActivity() {

    private val paths = ArrayList<File>()
    val handler = Handler()
    var count = 0
    private val iF = IntentFilter()

    private var lastActivity = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screensaver)


        val path = File(intent.extras?.getString(PATH) ?: "/1")
        if (!path.exists()) {
            finish()
            return
        }

        paths += (if (path.isFile) path.parentFile else path).listFiles()!!.filter { it.isImage() }

        paths.sortFiles()

        handler.post(object : Runnable {
            override fun run() {
                doAll()
                handler.postDelayed(this, 1000)
            }
        })

        iF.addAction("com.android.music.metachanged")
        iF.addAction("com.android.music.playstatechanged")
        iF.addAction("com.android.music.playbackcomplete")
        iF.addAction("com.android.music.queuechanged")
        registerReceiver(musicReceiver, iF)

        lastActivity = now()

        image.setOnClickListener { lastActivity = now() }
    }

    override fun onStart() {
        super.onStart()
        stars.onStart()
    }

    override fun onStop() {
        stars.onStop()
        super.onStop()
    }

    private fun doAll() {
        count--
        updateText()
        if (count < 0) {
            showImage()
            count = 10
        }
    }

    private fun canUpdate(): Boolean = if (now() - lastActivity > 15.minutes()) {
        date.hide()
        time.hide()
        songinfo.hide()
        batteryinfo.hide()
        false
    } else {
        date.show()
        time.show()
        songinfo.show()
        batteryinfo.show()
        true
    }

    private fun updateText() {
        if (!canUpdate()) return

        if (count < 0) batteryinfo.text = "%.0f %%".format(getBatteryStat())
        time.text = Date().toTimeShort()
        date.text = Date().toDatePretty()
    }

    private fun getBatteryStat(): Float {

        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return if (level == -1 || scale == -1) 50.0f else level.toFloat() / scale.toFloat() * 100.0f
    }

    override fun onBackPressed() {
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(musicReceiver)
        super.onBackPressed()
    }

    private fun showImage() {
        paths.random().apply {
            if (exists()) {
                Glide.with(this@ScreensaverActivity)
                        .load(Uri.fromFile(this))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(image as ImageView)
            } else image.setImageResource(R.drawable.ic_block)
        }
    }

    private var musicReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent) {
            songinfo.text = """
                ${intent.getStringExtra("track") ?: ""}
                by ${intent.getStringExtra("artist") ?: ""}
                from ${intent.getStringExtra("album") ?: ""}
            """.trimIndent()
        }
    }
}
