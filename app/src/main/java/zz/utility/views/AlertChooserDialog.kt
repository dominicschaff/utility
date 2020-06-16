package zz.utility.views

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_alert_chooser.view.*
import zz.utility.R


fun Activity.chooser(title: String, options: Array<String>, optionsIcons: Array<Int>? = null, callback: (Int, String) -> Unit) {

    val l = this.layoutInflater.inflate(R.layout.activity_alert_chooser, null)
    val dialog = AlertDialog.Builder(this)
    dialog.setTitle(title)
    dialog.setView(l)
    val finalDialog = dialog.show()

    val grid = l.mainGrid

    val useIcons = optionsIcons != null && optionsIcons.size == options.size

    options.forEachIndexed { index, option ->
        val v = layoutInflater.inflate(R.layout.activity_alert_chooser_text, grid, false) as TextView

        v.text = option
        if (useIcons)
            v.setCompoundDrawablesWithIntrinsicBounds(optionsIcons!![index], 0, 0, 0)
        v.setOnClickListener {
            callback(index, option)
            finalDialog.dismiss()
        }
        grid.addView(v)
    }
}