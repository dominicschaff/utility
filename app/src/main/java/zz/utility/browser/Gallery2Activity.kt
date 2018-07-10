package zz.utility.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_gallery2.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Comparator

class Gallery2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery2)

        val path = File(intent.extras?.getString("folder") ?: return)

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

class GalleryPagerAdapter(private val files: ArrayList<File>, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = files.size

    override fun getItem(i: Int): Fragment {
        val fragment = DemoObjectFragment()
        fragment.arguments = Bundle().apply {
            putString(PATH, files[i].absolutePath)
            putInt(SPOT, i + 1)
            putInt(TOTAL, files.size)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence = files[position].name
}

const val PATH = "path"
const val SPOT = "spot"
const val TOTAL = "total"

class DemoObjectFragment : Fragment() {

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
            Glide
                    .with(this)
                    .load(Uri.fromFile(path))
                    .into(rootView.image)

            rootView.image_details.text = "${path.length().formatSize()} [$spot/$total]"

            rootView.change_visibility.setOnClickListener {
                rootView.fab_delete.see()
                rootView.fab_open.see()
                rootView.fab_share.see()
                rootView.change_visibility.unsee()
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