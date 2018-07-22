package zz.utility.scrum

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v13.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_scrum_poker.*
import kotlinx.android.synthetic.main.fragment_scrum_poker.view.*
import zz.utility.R

class ScrumPokerActivity : Activity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrum_poker)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(fragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = newInstance(position)

        override fun getCount(): Int = 16

        override fun getPageTitle(position: Int): CharSequence? = "title"
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

    fun newInstance(sectionNumber: Int): PlaceholderFragment {
        val fragment = PlaceholderFragment()
        val args = Bundle()
        args.putInt("page_number", sectionNumber)
        fragment.arguments = args
        return fragment
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_scrum_poker, container, false)
            val imageIcon = when (arguments.getInt("page_number")) {
                0 -> {
                    rootView.card_number.text = "0 - Here be zero"
                    R.drawable.scrum_0
                }
                1 -> {
                    rootView.card_number.text = "½ - An Awake Koala"
                    R.drawable.scrum_1_2
                }
                2 -> {
                    rootView.card_number.text = "1 - Low hanging fruit"
                    R.drawable.scrum_1
                }
                3 -> {
                    rootView.card_number.text = "2 - Piece of cake"
                    R.drawable.scrum_2
                }
                4 -> {
                    rootView.card_number.text = "3 - It Ain’t Rocket Science"
                    R.drawable.scrum_3
                }
                5 -> {
                    rootView.card_number.text = "5 - Ornitorinco (Platypus)"
                    R.drawable.scrum_5
                }
                6 -> {
                    rootView.card_number.text = "8 - An arm and a leg"
                    R.drawable.scrum_8
                }
                7 -> {
                    rootView.card_number.text = "13 - Just squeaking by"
                    R.drawable.scrum_13
                }
                8 -> {
                    rootView.card_number.text = "20 - Don’t put all your eggs in one basket"
                    R.drawable.scrum_20
                }
                9 -> {
                    rootView.card_number.text = "40 - To step into an aubergine field"
                    R.drawable.scrum_40
                }
                10 -> {
                    rootView.card_number.text = "100 - Monster task (Oh, the poor unicorn is so scared!)"
                    R.drawable.scrum_100
                }
                11 -> {
                    rootView.card_number.text = "Infinite - When pigs fly (Wouldn’t that be awesome?)"
                    R.drawable.scrum_infinite
                }
                12 -> {
                    rootView.card_number.text = "? - Here be dragons"
                    R.drawable.scrum_dragons
                }
                13 -> {
                    rootView.card_number.text = "Ping Pong - Coffee break card"
                    R.drawable.scrum_break
                }
                14 -> {
                    rootView.card_number.text = "Brownie. Stuck with a difficult or unpleasant task"
                    R.drawable.scrum_brownie
                }
                15 -> {
                    rootView.card_number.text = "Yak shaving"
                    R.drawable.scrum_yak
                }
                else -> {
                    rootView.card_number.text = "unknown"
                    R.drawable.scrum_cover
                }
            }
            var vis = false
            val image = rootView.image
            image.setImageResource(R.drawable.scrum_cover)
            image.setOnClickListener {
                image.setImageResource(if (vis) R.drawable.scrum_cover else imageIcon)
                vis = !vis
            }
            return rootView
        }
    }
}
