package zz.utility.browser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import zz.utility.*
import zz.utility.helpers.formatSize
import zz.utility.helpers.longToast
import zz.utility.helpers.openFile
import java.io.File


class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.title)
    val img: ImageView = view.findViewById(R.id.img)
    val description: TextView = view.findViewById(R.id.description)
}

class MyFileAdapter(private val activity: Activity, private val galleryList: ArrayList<File>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.view_file, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val f = galleryList[i]

        viewHolder.title.text = f.name
        if (f.exists()) {
            if (f.isFile) {
                viewHolder.description.text = f.length().formatSize()
            } else viewHolder.description.text = "DIR"

            if (f.isImage() || f.isVideo()) {
                viewHolder.img.scaleType = ImageView.ScaleType.CENTER_CROP
                Glide
                        .with(activity)
                        .asBitmap()
                        .load(Uri.fromFile(f))
                        .thumbnail(0.1f)
                        .into(viewHolder.img)
            } else {
                viewHolder.img.setImageResource(f.imageIcon())
                viewHolder.img.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        } else {
            viewHolder.img.setImageResource(R.drawable.ic_block)
            viewHolder.description.text = "GONE"
        }

        viewHolder.view.setOnClickListener {
            when {
                f.isDirectory -> activity.startActivity(Intent(activity, FileBrowserActivity::class.java).putExtra("file_path", f.absolutePath))
                f.isImage() -> activity.startActivity(Intent(activity, GalleryActivity::class.java).putExtra("folder", f.absolutePath))
                f.isText() -> activity.startActivity(Intent(activity, TextViewActivity::class.java).putExtra("file", f.absolutePath))
                else -> activity.openFile(f)
            }
        }
        viewHolder.view.setOnLongClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(f.name)
                    .setItems(R.array.file_actions, { _, which ->
                        when (which) {
                            0 ->
                                when {
                                    f.isDirectory -> activity.startActivity(Intent(activity, FileBrowserActivity::class.java).putExtra("file_path", f.absolutePath))
                                    f.isImage() -> activity.startActivity(Intent(activity, GalleryActivity::class.java).putExtra("folder", f.absolutePath))
                                    f.isText() -> activity.startActivity(Intent(activity, TextViewActivity::class.java).putExtra("file", f.absolutePath))
                                    else -> activity.openFile(f)
                                }
                            1 -> {
                                // do move
                            }
                            3 -> {
                                // do slideshow
                                activity.startActivity(Intent(activity, Gallery2Activity::class.java).putExtra("folder", f.absolutePath))
                            }
                            4 -> {
                                val bin = File(Environment.getExternalStorageDirectory(), ".bin")
                                if (!bin.exists()) bin.mkdir()
                                if (!f.renameTo(File(bin, f.name))) activity.longToast("File could not be moved")
                                viewHolder.img.setImageResource(R.drawable.ic_delete)
                            }
                            else -> activity.openFile(f)
                        }
                    })
            builder.show()
            true
        }
    }

    override fun getItemCount(): Int = galleryList.size

}