package zz.utility.browser

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_quick_sort.*
import zz.utility.R
import zz.utility.helpers.alert
import zz.utility.helpers.formatSize
import zz.utility.helpers.toast
import zz.utility.isImage
import zz.utility.metaDataShort
import java.io.File

class QuickSortActivity : AppCompatActivity() {

    private val paths = ArrayList<File>()
    private var current = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_sort)

        val path = File(intent.extras?.getString(PATH) ?: "/1")
        if (!path.exists()) {
            finish()
            return
        }

        val basePath = if (path.isFile) path.parentFile else path

        paths += basePath.listFiles().filter { it.isImage() }

        paths.sortFiles()

        if (paths.isEmpty()) {
            alert("No images")
            return
        }

        fab_skip.setOnClickListener {
            moveOn()
        }
        fab_keep.setOnClickListener {
            val keep = File(basePath, "keep")
            if (!keep.exists() || !keep.isDirectory) keep.mkdir()
            File(keep, paths[current].name).let { newFile ->
                if (newFile.exists()) alert("There already exists the same file in directory ${newFile.parentFile.name}")
                else {
                    paths[current].renameTo(newFile)
                    moveOn()
                }
            }
        }

        fab_delete.setOnClickListener {
            if (paths[current].moveToBin()) moveOn()
            else toast("File could not be moved")

        }

        if (path.isFile)
            current = paths.indexOfFirst { it.name == path.name }

        if (current < 0) current = 0
        showImage()
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

    @SuppressLint("SetTextI18n")
    private fun showImage() {

        val path = paths[current]
        info.text = "${current + 1} / ${paths.size} | ${path.length().formatSize()} | ${path.metaDataShort().trim()} | ${path.absolutePath}"

        if (path.exists()) {
            Glide.with(this)
                    .load(Uri.fromFile(path))
                    .into(image as ImageView)
        } else image.setImageResource(R.drawable.ic_block)
    }
}
