package zz.utility.launcher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.fragment_launcher_main.view.*
import zz.utility.MAIN
import zz.utility.R
import zz.utility.helpers.*


class MainFragment : androidx.fragment.app.Fragment() {
    private lateinit var mDetector: GestureDetectorCompat
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_launcher_main, null)


        val list = MAIN.fileAsJsonObject().o("launcher").a("fave").map { it.asString }
        val pm = activity!!.packageManager

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0).asSequence().map { AppInfo(it.loadLabel(pm), it.activityInfo.packageName) }

        list.forEach { fave ->
            val p = allApps.firstOrNull() { it.packageName == fave }
            if (p != null) {
                val im = layoutInflater.inflate(R.layout.launcher_icon_main, view.grid, false) as TextView
                im.text = p.label.firstLetters()
                im.setOnLongClickListener { consume { context?.toast(p.label.toString()) } }
                im.setOnClickListener { context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(p.packageName.toString())) }
                view.grid.addView(im)
            }
        }

        mDetector = GestureDetectorCompat(context, MyGestureListener(activity!!))

        view.setOnTouchListener { _, event -> mDetector.onTouchEvent(event) }

        return view
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
            if (event1.y < event2.y) {
                val sbservice = activity.getSystemService("statusbar")
                val statusbarManager = Class.forName("android.app.StatusBarManager")
                val showsb = statusbarManager.getMethod("expandNotificationsPanel")
                showsb.invoke(sbservice)
            }
            return true
        }
    }
}