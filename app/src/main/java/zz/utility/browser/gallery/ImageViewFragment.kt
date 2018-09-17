package zz.utility.browser.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import zz.utility.R
import zz.utility.browser.PATH
import zz.utility.browser.SPOT
import zz.utility.browser.TOTAL
import zz.utility.helpers.formatSize
import zz.utility.helpers.longToast
import zz.utility.helpers.openFile
import zz.utility.helpers.see
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
        rootView.path.text = path.name
        if (path.exists()) {
            Glide.with(this)
                    .load(Uri.fromFile(path))
                    .into(rootView.image)

            rootView.image_details.text = "${path.length().formatSize()} [$spot/$total]"

            rootView.image.setOnClickListener {
                rootView.actions.see()
            }
            rootView.fab_delete.setOnClickListener {
                val bin = File(Environment.getExternalStorageDirectory(), ".bin")
                if (!bin.exists()) bin.mkdir()
                if (!path.renameTo(File(bin, path.name))) context?.longToast("File could not be moved")
                rootView.image.setImageResource(R.drawable.ic_delete)
            }
            rootView.fab_open.setOnClickListener {
                activity?.openFile(path)
            }
            rootView.fab_share.setOnClickListener {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(path))
                shareIntent.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.extension)
                startActivity(Intent.createChooser(shareIntent, "Send image to..."))
            }
        } else {
            rootView.image.setImageResource(R.drawable.ic_block)
            rootView.image_details.text = "[$spot/$total]"
        }
        return rootView
    }
}