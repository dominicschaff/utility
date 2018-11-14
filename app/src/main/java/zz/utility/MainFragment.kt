package zz.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.BatteryManager
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.fragment_launcher_main.view.*
import zz.utility.helpers.*


class MainFragment : androidx.fragment.app.Fragment() {
    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var stats: TextView
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_launcher_main, null)

        val pm = context!!.packageManager

        MAIN.fileAsJsonObject().o("launcher").a("fave").map {
            doLoad(view.grid, it.asString, pm)
        }
        mDetector = GestureDetectorCompat(context, MyGestureListener(activity!!))

        view.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
        }

        stats = view.stats

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val a = activity ?: return

        val batteryIntent = a.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val temp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0

        val battery = if (level == -1 || scale == -1) 50.0f else level.toFloat() / scale.toFloat() * 100.0f

        val actManager = a.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)

        val mif = Utilities.getFreeInternalMemory(a)
        val mit = Utilities.getTotalInternalMemory(a)
        val f = Utilities.getFreeExternalMemory(a)
        val t = Utilities.getTotalExternalMemory(a)
        val o = (1 until f.size).joinToString("\n") { "External : ${(t[it] - f[it]).formatSize()} / ${t[it].formatSize()}" }

        stats.text = """$battery % ($temp)
            |${(memInfo.totalMem - memInfo.availMem).formatSize()} / ${memInfo.totalMem.formatSize()} [${(memInfo.totalMem - memInfo.threshold).formatSize()}]
            |Internal : ${(mit - mif).formatSize()} / ${mit.formatSize()}
            |$o
        """.trimMargin()
    }

    private fun doLoad(gl: GridLayout, packageName: String, packageManager: PackageManager) {
        val d = getActivity(packageName) ?: return
        val im = layoutInflater.inflate(R.layout.launcher_icon_main, gl, false) as TextView

        val s = d.loadLabel(packageManager)

        im.text = s.firstLetters()

        im.setOnLongClickListener { consume { context?.toast(s.toString()) } }
        im.setOnClickListener { context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(d.activityInfo.packageName.toString())) }
        gl.addView(im)
    }

    private fun getActivity(packageName: String): ResolveInfo? {
        val pm = context!!.packageManager
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.`package` = packageName

        val p = pm.queryIntentActivities(intent, 0)

        return p.firstOrNull()
    }

    class MyGestureListener(val activity: Activity) : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean = true

        @SuppressLint("WrongConstant", "PrivateApi")
        override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float
        ): Boolean {
            if (event1.y > event2.y) {
                activity.startActivity(activity.packageManager.getLaunchIntentForPackage("com.google.android.googlequicksearchbox"))
            } else {
                val sbservice = activity.getSystemService("statusbar")
                val statusbarManager = Class.forName("android.app.StatusBarManager")
                val showsb = statusbarManager.getMethod("expandNotificationsPanel")
                showsb.invoke(sbservice)
            }
            return true
        }
    }
}