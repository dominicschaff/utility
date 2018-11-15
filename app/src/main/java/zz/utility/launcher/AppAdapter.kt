package zz.utility.launcher

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import zz.utility.R

class AppAdapter(private val context: Context, private val appsList: Array<AppInfo>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.launcher_icon, viewGroup, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.title.text = appsList[i].label
        viewHolder.subtitle.text = appsList[i].packageName
        viewHolder.img.text = appsList[i].label.firstLetters()
        viewHolder.view.setOnClickListener {
            context.startActivity(context.packageManager.getLaunchIntentForPackage(appsList[i].packageName.toString()))
        }

    }

    override fun getItemCount(): Int = appsList.size


}