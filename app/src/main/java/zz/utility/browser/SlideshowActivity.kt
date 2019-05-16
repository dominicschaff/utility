package zz.utility.browser

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_slideshow.*
import zz.utility.R
import zz.utility.helpers.PipActivity
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Comparator

class SlideshowActivity : PipActivity() {

    private val paths = ArrayList<File>()
    private var current = 0
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_slideshow)

        val path = File(intent.extras?.getString(PATH) ?: return)
        val delay = if (intent.extras?.getBoolean(SLOW, false) == true) 5000L else 1000L

        paths += (if (path.isFile) path.parentFile else path).listFiles().filter { it.isImage() }

        paths.sortWith(Comparator { o1, o2 ->
            o1.name.compareTo(o2.name, ignoreCase = true)
        })

        if (path.isFile)
            current = paths.indexOfFirst { it.name == path.name }

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
                    .into(image as ImageView)
        } else image.setImageResource(R.drawable.ic_block)
    }
}
