package zz.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(private val context: Context, private val appsList: Array<AppInfo>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.launcher_icon, viewGroup, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.title.text = appsList[i].label.toString()

        val b = getAppIcon(context.packageManager, appsList[i].packageName.toString())

        if (b != null) viewHolder.img.setImageBitmap(b)
        else viewHolder.img.setImageDrawable(appsList[i].icon)


        viewHolder.view.setOnClickListener {
            context.startActivity(context.packageManager.getLaunchIntentForPackage(appsList[i].packageName.toString()))
        }

    }

    override fun getItemCount(): Int = appsList.size


}