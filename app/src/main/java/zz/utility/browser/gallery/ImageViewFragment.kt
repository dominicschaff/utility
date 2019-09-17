package zz.utility.browser.gallery

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import zz.utility.BuildConfig
import zz.utility.R
import zz.utility.browser.PATH
import zz.utility.browser.SPOT
import zz.utility.browser.TOTAL
import zz.utility.browser.moveToBin
import zz.utility.helpers.formatSize
import zz.utility.helpers.see
import zz.utility.helpers.toast
import zz.utility.metaData
import java.io.File


class ImageViewFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val bundle = arguments
        if (bundle == null) {
            val tv = TextView(context)
            tv.text = "Error with load"
            return tv
        }
        val path = File(bundle.getString(PATH))
        val spot = bundle.getInt(SPOT)
        val total = bundle.getInt(TOTAL)
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)
        rootView.image_details.text = "[$spot/$total]"

        rootView.path.text = path.metaData() + path.name
        if (path.exists()) {
            Glide.with(this)
                    .load(Uri.fromFile(path))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(rootView.image as ImageView)

            rootView.image_size.text = path.length().formatSize()

            rootView.image.setOnClickListener {
                rootView.fab_set_as_wallpaper.see()
                rootView.fab_delete.see()
                rootView.image_size.see()
                rootView.path.see()
            }
            rootView.fab_delete.setOnClickListener {
                if (path.moveToBin()) rootView.image.setImageResource(R.drawable.ic_delete)
                else it.context?.toast("File could not be moved")
            }
            rootView.fab_set_as_wallpaper.setOnClickListener { setAsWallpaper(path) }
        } else rootView.image.setImageResource(R.drawable.ic_block)
        return rootView
    }

    private fun setAsWallpaper(file: File) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_ATTACH_DATA
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", file), getMimeType(file.absolutePath))
            context!!.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context?.toast("No application to handle wallpapers")
        }
    }


    private fun getMimeType(url: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return if (extension != null) MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) else null
    }
}