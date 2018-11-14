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

        return view
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