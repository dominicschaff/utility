package zz.utility.launcher

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_launcher.*
import zz.utility.R

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        pager.adapter = SectionsPagerAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 2
        pager.currentItem = 0
    }

    override fun onBackPressed() {
        pager.currentItem = 0
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = if (position == 0) MainFragment() else UtilityFragment()

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? = "Page $position"
    }
}
