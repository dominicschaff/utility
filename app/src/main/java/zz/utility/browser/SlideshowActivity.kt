package zz.utility.browser

import android.app.PictureInPictureParams
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_slideshow.*
import zz.utility.R
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Comparator


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SlideshowActivity : AppCompatActivity() {

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

    override fun onUserLeaveHint() {
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
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
