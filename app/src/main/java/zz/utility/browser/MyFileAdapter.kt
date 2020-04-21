package zz.utility.browser

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import zz.utility.*
import zz.utility.browser.gallery.GalleryActivity
import zz.utility.helpers.*
import zz.utility.views.chooser
import zz.utility.views.playAudio
import java.io.File

class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.title)
    val img: ImageView = view.findViewById(R.id.img)
    val description: TextView = view.findViewById(R.id.description)
}

class MyFileAdapter(private val activity: FileBrowserActivity, private val galleryList: ArrayList<File>, private val folderList: ArrayList<File>) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.view_file, viewGroup, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val f = galleryList[i]

        viewHolder.title.text = f.name
        when {
            f.isDirectory -> {
                viewHolder.img.setImageResource(R.drawable.ic_file_folder)
                viewHolder.description.text = "DIR"
                viewHolder.img.setPadding(10, 10, 10, 10)
            }
            f.exists() -> {
                viewHolder.description.text = f.length().formatSize()

                when {
                    f.isImage() or f.isVideo() -> {
                        Glide
                                .with(activity)
                                .asBitmap()
                                .load(Uri.fromFile(f))
                                .thumbnail(0.1f)
                                .into(viewHolder.img)
                        viewHolder.img.setPadding(0, 0, 0, 0)
                    }
                    else -> {
                        viewHolder.img.setImageResource(f.imageIcon())
                        viewHolder.img.setPadding(10, 10, 10, 10)
                    }
                }
            }
            else -> {
                viewHolder.img.setImageResource(R.drawable.ic_block)
                viewHolder.description.text = "GONE"
            }
        }

        viewHolder.view.setOnClickListener {
            when {
                f.isDirectory -> activity.startActivity(Intent(activity, FileBrowserActivity::class.java).putExtra(PATH, f.absolutePath))
                f.isImage() -> activity.startActivity(Intent(activity, GalleryActivity::class.java).putExtra(PATH, f.absolutePath))
                f.isVideo() -> activity.startActivity(Intent(activity, VideoPlayerActivity::class.java).putExtra(PATH, f.absolutePath))
                f.isText() -> activity.startActivity(Intent(activity, TextViewActivity::class.java).putExtra(PATH, f.absolutePath))
                f.isAudio() -> activity.playAudio(f)
                else -> activity.openFile(f)
            }
        }
        viewHolder.view.setOnLongClickListener {
            activity.chooser(f.name, activity.resources.getStringArray(R.array.file_actions)) { action, _ ->
                when (action) {
                    0 -> {
                        activity.chooser("Select destination folder", folderList.map { it.name }.toTypedArray(), callback = { option, _ ->
                            File(folderList[option], f.name).let { newFile ->
                                if (newFile.exists()) activity.alert("There already exists the same file in directory ${newFile.parentFile!!.name}")
                                else {
                                    f.renameTo(newFile)
                                    activity.refreshList()
                                }
                            }
                        }
                        )
                    }
                    1 -> {
                        f.parentFile!!.parentFile.let { parent ->
                            File(parent, f.name).let { newFile ->
                                if (newFile.exists()) activity.alert("There already exists the same file in directory above")
                                else {
                                    f.renameTo(newFile)
                                    activity.refreshList()
                                }
                            }
                        }
                    }
                    2 -> activity.startActivity(Intent(activity, QuickSortActivity::class.java).putExtra(PATH, f.absolutePath))
                    3 -> activity.alert("Total file size is ${f.getFileSize().formatSize()}\nTotal Files: ${f.getFileCount()}")
                    4 -> activity.startActivity(Intent(activity, SlideshowActivity::class.java).putExtra(PATH, f.absolutePath))
                    5 -> activity.startActivity(Intent(activity, SlideshowActivity::class.java).putExtra(PATH, f.absolutePath).putExtra(SLOW, true))
                    6 -> {
                        if (f.moveToBin()) activity.refreshList()
                        else activity.toast("File could not be moved")
                    }
                    7 -> {
                        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText(f.absolutePath, f.absolutePath))
                        activity.toast("Set clipboard to: ${f.absolutePath}")
                    }
                    8 -> when {
                        f.isDirectory -> activity.startActivity(Intent(activity, FileBrowserActivity::class.java).putExtra(PATH, f.absolutePath))
                        f.isImage() -> activity.startActivity(Intent(activity, GalleryActivity::class.java).putExtra(PATH, f.absolutePath))
                        f.isVideo() -> activity.startActivity(Intent(activity, VideoPlayerActivity::class.java).putExtra(PATH, f.absolutePath))
                        f.isText() -> activity.startActivity(Intent(activity, TextViewActivity::class.java).putExtra(PATH, f.absolutePath))
                        else -> activity.openFile(f)
                    }
                    9 -> activity.openFile(f)
                    else -> activity.openFile(f)
                }
            }
            true
        }
    }

    override fun getItemCount(): Int = galleryList.size
}