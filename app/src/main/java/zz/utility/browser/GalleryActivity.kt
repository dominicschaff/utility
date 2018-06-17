package zz.utility.browser

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v13.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import zz.utility.R
import zz.utility.helpers.formatSize
import zz.utility.helpers.longToast
import zz.utility.helpers.openFile
import zz.utility.isImage
import java.io.File
import java.util.*

class GalleryActivity : Activity() {

    val paths = ArrayList<File>()

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        mSectionsPagerAdapter = SectionsPagerAdapter(fragmentManager)

        val path = File(intent.extras?.getString("folder") ?: return)

        paths.addAll(path.parentFile.listFiles().filter { it.isImage() })

        paths.sortWith(Comparator { o1, o2 ->
            o1.name.toLowerCase().compareTo(o2.name.toLowerCase())
        })

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        val page: Int = paths.indexOfFirst { it.name == path.name }
        container.currentItem = page
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

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = PlaceholderFragment.newInstance(paths[position].absolutePath, position, paths.size)

        override fun getCount(): Int = paths.size

        override fun getPageTitle(position: Int): CharSequence? = paths[position].absolutePath
    }

    class PlaceholderFragment : Fragment() {

        @SuppressLint("SetTextI18n")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val path = File(arguments.getString(IMAGE_PATH))
            val spot = arguments.getInt(IMAGE_SPOT)
            val total = arguments.getInt(IMAGE_TOTAL)
            val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)
            rootView.path.text = path.absolutePath
            if (path.exists()) {
                Glide
                        .with(this)
                        .load(Uri.fromFile(path))
                        .into(rootView.image)

                rootView.image_details.text = "${path.length().formatSize()} [$spot/$total]"

                rootView.fab_delete.setOnClickListener {
                    val bin = File(Environment.getExternalStorageDirectory(), ".bin")
                    if (!bin.exists()) bin.mkdir()
                    if (!path.renameTo(File(bin, path.name))) context.longToast("File could not be moved")
                    rootView.image.setImageResource(R.drawable.ic_delete)
                }
                rootView.fab_open.setOnClickListener {
                    activity.openFile(path)
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

        companion object {
            private const val IMAGE_PATH = "image_view"
            private const val IMAGE_SPOT = "image_spot"
            private const val IMAGE_TOTAL = "image_total"

            fun newInstance(path: String, spot: Int, total: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putString(IMAGE_PATH, path)
                args.putInt(IMAGE_SPOT, spot + 1)
                args.putInt(IMAGE_TOTAL, total)
                fragment.arguments = args
                return fragment
            }
        }
    }

}
