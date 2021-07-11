package zz.utility.views

import android.app.Activity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import zz.utility.R


fun Activity.chooser(title: String, options: Array<String>, callback: (Int, String) -> Unit) {

    val l = this.layoutInflater.inflate(R.layout.activity_alert_chooser, null)
    val dialog = AlertDialog.Builder(this)
    dialog.setTitle(title)
    dialog.setView(l)
    val finalDialog = dialog.show()

    val grid = l.findViewById<GridLayout>(R.id.mainGrid)

    options.forEachIndexed { index, option ->
        val v = layoutInflater.inflate(R.layout.activity_alert_chooser_text, grid, false) as TextView

        v.text = option
        v.setOnClickListener {
            callback(index, option)
            finalDialog.dismiss()
        }
        grid.addView(v)
    }
}