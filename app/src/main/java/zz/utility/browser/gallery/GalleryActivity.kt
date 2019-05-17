package zz.utility.browser.gallery

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_gallery.*
import zz.utility.R
import zz.utility.browser.PATH
import zz.utility.helpers.PipActivity
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Comparator

class GalleryActivity : PipActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val path = File(intent.extras?.getString(PATH) ?: "/1")
        if (!path.exists()) {
            finish()
            return
        }

        val paths = ArrayList<File>()
        paths.addAll(path.parentFile.listFiles().filter { it.isImage() })

        paths.sortWith(Comparator { o1, o2 ->
            o1.name.toLowerCase().compareTo(o2.name.toLowerCase())
        })
        pager.adapter = GalleryPagerAdapter(paths, supportFragmentManager)

        val page: Int = paths.indexOfFirst { it.name == path.name }
        pager.currentItem = page
    }
}