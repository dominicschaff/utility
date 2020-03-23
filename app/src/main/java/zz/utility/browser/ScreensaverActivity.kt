package zz.utility.browser

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_screensaver.*
import zz.utility.R
import zz.utility.helpers.FullscreenActivity
import zz.utility.helpers.shortTime
import zz.utility.isImage
import java.io.File
import java.util.*


class ScreensaverActivity : FullscreenActivity() {

    private val paths = ArrayList<File>()
    val handler = Handler()
    var count = 0
    val iF = IntentFilter()

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
    }

    private fun doAll() {
        count--
        if (count < 0) {
            showImage()
            count = 10
        }
        time.text = Date().shortTime()
    }

    override fun onBackPressed() {
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(musicReceiver);
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
