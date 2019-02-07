package zz.utility.launcher

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_launcher_main.*
import kotlinx.android.synthetic.main.fragment_launcher_main.view.*
import zz.utility.MAIN
import zz.utility.R
import zz.utility.helpers.*


class MainFragment : androidx.fragment.app.Fragment() {
    private lateinit var stats: TextView
    //    private lateinit var mDetector: GestureDetectorCompat
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_launcher_main, null)


        val config = MAIN.fileAsJsonObject().o("launcher")
        val favourites = config.a("fave").map { it.asString }
        val hidden = config.a("hide").map { it.asString }
        val pm = activity!!.packageManager

        view.recycler_view.layoutManager = GridLayoutManager(context!!, if (resources.getBoolean(R.bool.is_landscape)) 3 else 2)
        view.grid.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 8 else 5

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0).asSequence().map { AppInfo(it.loadLabel(pm), it.activityInfo.packageName) }

        favourites.forEach { fave ->
            val p = allApps.firstOrNull() { it.packageName == fave }
            if (p != null) {
                val im = layoutInflater.inflate(R.layout.launcher_icon_main, view.grid, false) as TextView
                im.text = p.label.firstLetters()
                im.setOnLongClickListener { consume { context?.toast(p.label.toString()) } }
                im.setOnClickListener { context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(p.packageName.toString())) }
                view.grid.addView(im)
            }
        }

        val appsList: Array<AppInfo> = allApps.filter {
            !hidden.contains(it.packageName.toString()) && it.packageName.toString() != "zz.utility" && it.packageName.toString() != "com.android.vending"
        }.toList().toTypedArray()

        appsList.sortWith(Comparator { o1, o2 ->
            o1.label.toString().compareTo(o2.label.toString(), true)
        })

        view.recycler_view.adapter = AppAdapter(context!!, appsList)

        view.play_store_link.setOnClickListener {
            context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage("com.android.vending"))
        }

        stats = view.stats
        stats.setOnClickListener { updateStats() }

//        mDetector = GestureDetectorCompat(context, MyGestureListener(activity!!))

//        view.setOnTouchListener { _, event -> mDetector.onTouchEvent(event) }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    @SuppressLint("SetTextI18n")
    private fun updateStats() {

        val a = activity ?: return

        val batteryIntent = a.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val temp = (batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0).toInt()

        val battery = (if (level == -1 || scale == -1) 50.0f else level.toFloat() / scale.toFloat() * 100.0f).toInt()

        val actManager = a.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val mif = Utilities.getFreeInternalMemory(a)
        val mit = Utilities.getTotalInternalMemory(a)
        val f = Utilities.getFreeExternalMemory(a)
        val t = Utilities.getTotalExternalMemory(a)
        val o = (1 until f.size).joinToString("\n") { "External : ${(t[it] - f[it]).formatSize()} / ${t[it].formatSize()}" }

        stats.text = """Battery  : $battery% | $temp°
            |Memory   : ${(memInfo.totalMem - memInfo.availMem).formatSize()} / ${memInfo.totalMem.formatSize()}
            |Internal : ${(mit - mif).formatSize()} / ${mit.formatSize()}
            |$o
        """.trimMargin()
    }

//    class MyGestureListener(val activity: Activity) : GestureDetector.SimpleOnGestureListener() {
//
//        override fun onDown(event: MotionEvent): Boolean = true
//
//        @SuppressLint("WrongConstant", "PrivateApi")
//        override fun onFling(
//                event1: MotionEvent,
//                event2: MotionEvent,
//                velocityX: Float,
//                velocityY: Float
//        ): Boolean {
//            if (event1.y < event2.y) {
//                val sbservice = activity.getSystemService("statusbar")
//                val statusbarManager = Class.forName("android.app.StatusBarManager")
//                val showsb = statusbarManager.getMethod("expandNotificationsPanel")
//                showsb.invoke(sbservice)
//            }
//            return true
//        }
//    }
}