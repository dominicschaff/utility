package zz.utility.browser

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_slideshow.*
import zz.utility.R
import zz.utility.helpers.PipActivity
import zz.utility.isImage
import java.io.File
import java.util.*

class SlideshowActivity : PipActivity() {

    private val paths = ArrayList<File>()
    private var current = 0
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_slideshow)

        val path = File(intent.extras?.getString(PATH) ?: "/1")
        if (!path.exists()) {
            finish()
            return
        }
        val delay = if (intent.extras?.getBoolean(SLOW, false) == true) 5000L else 1000L

        paths += (if (path.isFile) path.parentFile else path).listFiles().filter { it.isImage() }

        paths.sortFiles()

        if (path.isFile)
            current = paths.indexOfFirst { it.name == path.name }
        if (current < 0) current = 0
        showImage()
        handler.postDelayed(object : Runnable {
            override fun run() {
                moveOn()
                handler.postDelayed(this, delay)
            }
        }, delay)
    }

    override fun onBackPressed() {
        handler.removeCallbacksAndMessages(null)
        super.onBackPressed()
    }

    private fun moveOn() {
        current = (current + 1) % paths.size
        showImage()
    }

    private fun showImage() {

        val path = paths[current]

        if (path.exists()) {
            Glide.with(this)
                    .load(Uri.fromFile(path))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image as ImageView)
        }
        else image.setImageResource(R.drawable.ic_block)
    }
}
