package zz.utility

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        pager.adapter = SectionsPagerAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 2
        pager.currentItem = 1
    }

    override fun onBackPressed() {
        pager.currentItem = 1
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> AllAppsFragment()
            1 -> MainFragment()
            else -> UtilityFragment()
        }

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> "All Apps"
            1 -> "Main"
            2 -> "Utility"
            else -> null
        }
    }
}
