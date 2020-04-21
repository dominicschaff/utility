package zz.utility.views

import android.app.Activity
import android.media.MediaPlayer
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_alert_chooser.view.*
import kotlinx.android.synthetic.main.alert_audio.view.*
import zz.utility.R
import zz.utility.helpers.toTimeFormat
import java.io.File


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

fun Activity.playAudio(file: File) {
    val l = this.layoutInflater.inflate(R.layout.alert_audio, null)
    val songInfo = l.song_info
    val dialog = AlertDialog.Builder(this).apply {
        setTitle(file.name)
        setView(l)
    }.show()
    val mp: MediaPlayer = MediaPlayer.create(this, file.toUri())
    songInfo.text = mp.duration.toLong().toTimeFormat()
    mp.setOnCompletionListener {
        mp.release()
        dialog.dismiss()
    }
    mp.start()
    dialog.setOnCancelListener {
        mp.stop()
        mp.release()
    }
}