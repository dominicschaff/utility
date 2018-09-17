package zz.utility.browser.gallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_gallery2.*
import zz.utility.R
import zz.utility.browser.PATH
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Comparator

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery2)

        val path = File(intent.extras?.getString(PATH) ?: return)

        val paths = ArrayList<File>()
        paths.addAll(path.parentFile.listFiles().filter { it.isImage() })

        paths.sortWith(Comparator { o1, o2 ->
            o1.name.toLowerCase().compareTo(o2.name.toLowerCase())
        })
        pager.adapter = GalleryPagerAdapter(paths, supportFragmentManager)

        val page: Int = paths.indexOfFirst { it.name == path.name }
        pager.currentItem = page
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
}