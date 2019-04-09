package zz.utility.browser

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_quick_sort.*
import zz.utility.R
import zz.utility.helpers.alert
import zz.utility.helpers.toast
import zz.utility.isImage
import zz.utility.metaData
import java.io.File

class QuickSortActivity : AppCompatActivity() {

    private val paths = ArrayList<File>()
    private var current = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_sort)

        var path = File(intent.extras?.getString(PATH) ?: return)
        if (path.isFile) {
            path = path.parentFile
        }

        paths += path.listFiles().filter { it.isImage() }

        paths.sortWith(Comparator { o1, o2 ->
            o1.name.compareTo(o2.name, ignoreCase = true)
        })

        if (paths.isEmpty()) {
            alert("No images")
            return
        }

        showImage()

        fab_next.setOnClickListener {
            moveOn()
        }
        fab_previous.setOnClickListener {
            moveBack()
        }

        fab_delete.setOnClickListener {
            val bin = File(Environment.getExternalStorageDirectory(), ".bin")
            if (!bin.exists()) bin.mkdir()
            if (!paths[current].renameTo(File(bin, paths[current].name))) toast("File could not be moved")
            image.setImageResource(R.drawable.ic_delete)
            moveOn()
        }
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

    private fun moveBack() {
        current = (current - 1 + paths.size) % paths.size
        showImage()
    }

    @SuppressLint("SetTextI18n")
    private fun showImage() {

        val path = paths[current]
        info.text = "${current + 1} / ${paths.size}\n${path.absolutePath}\n${path.metaData().trim()}"

        if (path.exists()) {
            Glide.with(this)
                    .load(Uri.fromFile(path))
                    .into(image as ImageView)
        } else image.setImageResource(R.drawable.ic_block)
    }
}
