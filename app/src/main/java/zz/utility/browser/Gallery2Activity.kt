package zz.utility.browser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_gallery2.*
import zz.utility.R
import zz.utility.helpers.longToast
import zz.utility.isImage
import java.io.File
import java.util.ArrayList
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparator
import kotlin.Int
import kotlin.apply

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
        pager.setOnClickListener { longToast("hi") }
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
            putString(ARG_OBJECT, files[i].absolutePath)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "OBJECT " + (position + 1)
    }
}

private const val ARG_OBJECT = "object"

class DemoObjectFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val textView = TextView(context)
        textView.text = arguments?.getString(ARG_OBJECT)
        return textView
    }
}