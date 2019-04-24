package zz.utility.browser

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_sorter.*
import zz.utility.R
import zz.utility.helpers.alert
import zz.utility.helpers.toast
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Comparator

class SorterActivity : AppCompatActivity() {

    private val paths = ArrayList<File>()
    private val folders = ArrayList<File>()
    private var current = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorter)

        val path = File(intent.extras?.getString(PATH) ?: return)

        val allFiles = (if (path.isFile) path.parentFile else path).listFiles()

        paths += allFiles.filter { it.isImage() }
        folders += allFiles.filter { it.isDirectory }

        paths.sortWith(Comparator { o1, o2 ->
            o1.name.compareTo(o2.name, ignoreCase = true)
        })

        folders.sortWith(Comparator { o1, o2 ->
            o1.name.compareTo(o2.name, ignoreCase = true)
        })

        if (paths.isEmpty()) {
            alert("No images")
            return
        }

        fab_next.setOnClickListener {
            moveOn()
        }

        fab_delete.setOnClickListener {
            if (paths[current].moveToBin()) moveOn()
            else toast("File could not be moved")
        }

        folders.forEach { folder ->
            val t = layoutInflater.inflate(R.layout.sorter_folder, folder_list, false) as Button
            t.text = folder.name
            t.setOnClickListener {
                File(folder, paths[current].name).let { newFile ->
                    if (newFile.exists()) alert("There already exists the same file in directory ${newFile.parentFile.name}")
                    else {
                        paths[current].renameTo(newFile)
                        moveOn()
                    }
                }
            }
            folder_list.addView(t)
        }
        if (path.isFile)
            current = paths.indexOfFirst { it.name == path.name }

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
        info.text = "${current + 1} / ${paths.size}"

        if (path.exists()) {
            Glide.with(this)
                    .load(Uri.fromFile(path))
                    .into(image as ImageView)
        } else image.setImageResource(R.drawable.ic_block)
    }
}
