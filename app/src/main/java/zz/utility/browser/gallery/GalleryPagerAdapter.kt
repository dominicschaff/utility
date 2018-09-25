package zz.utility.browser.gallery

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import zz.utility.browser.PATH
import zz.utility.browser.SPOT
import zz.utility.browser.TOTAL
import java.io.File
import java.util.*

class GalleryPagerAdapter(private val files: ArrayList<File>, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = files.size

    override fun getItem(i: Int): androidx.fragment.app.Fragment {
        val fragment = ImageViewFragment()
        fragment.arguments = Bundle().apply {
            putString(PATH, files[i].absolutePath)
            putInt(SPOT, i + 1)
            putInt(TOTAL, files.size)
        }
        return fragment
    }

    fun delete(item: Int): Boolean {
        val path = files[item]
        val bin = File(Environment.getExternalStorageDirectory(), ".bin")
        if (!bin.exists()) bin.mkdir()
        return path.renameTo(File(bin, path.name))
    }

    override fun getPageTitle(position: Int): CharSequence = files[position].name
}