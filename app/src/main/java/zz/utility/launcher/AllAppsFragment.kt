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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_launcher_all.view.*
import zz.utility.MAIN
import zz.utility.R
import zz.utility.helpers.*


class AllAppsFragment : Fragment() {
    private lateinit var stats: TextView


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_launcher_all, null)

        view.recycler_view.layoutManager = GridLayoutManager(context!!, if (resources.getBoolean(R.bool.is_landscape)) 3 else 2)


        val pm = activity!!.packageManager

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0)
        val h = MAIN.fileAsJsonObject().o("launcher").a("hide").map { it.asString }
        val appsList: Array<AppInfo> = allApps.asSequence().map { AppInfo(it.loadLabel(pm), it.activityInfo.packageName) }.filter {
            !h.contains(it.packageName.toString()) && it.packageName.toString() != "zz.utility" && it.packageName.toString() != "com.android.vending"
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
}