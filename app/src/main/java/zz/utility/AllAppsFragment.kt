package zz.utility

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_launcher_all.view.*
import zz.utility.helpers.a
import zz.utility.helpers.fileAsJsonObject
import zz.utility.helpers.o


class AllAppsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_launcher_all, null)

        view.recycler_view.layoutManager = GridLayoutManager(context!!, if (resources.getBoolean(R.bool.is_landscape)) 3 else 1)

        val pm = context!!.packageManager

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = pm.queryIntentActivities(i, 0)
        val h = MAIN.fileAsJsonObject().o("launcher").a("hide").map { it.asString }
        val appsList = allApps.asSequence().map { AppInfo(it.loadLabel(pm), it.activityInfo.packageName) }.filter {
            !h.contains(it.packageName.toString())
        }.toList().toTypedArray()

        appsList.sortWith(Comparator { o1, o2 ->
            o1.label.toString().compareTo(o2.label.toString())
        })
        view.recycler_view.adapter = AppAdapter(context!!, appsList)

        return view

    }
}