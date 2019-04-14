package zz.utility.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alert_chooser.*
import zz.utility.R

const val ALERT_OPTION_CHOSE = 9283
const val RETURNED_INDEX = "returnedIndex"
const val RETURNED_VALUE = "returnedValue"
const val ALERT_EXTRA_VALUE = "extraValue"

fun Activity.chooser(title: String, options: Array<String>) {
    val intent = Intent(this, AlertChooserActivity::class.java)
    intent.putExtra("TITLE", title)
    intent.putExtra("OPTIONS", options)
    startActivityForResult(intent, ALERT_OPTION_CHOSE)
}

fun Activity.chooser(title: String, optionsText: Array<String>, optionsImages: Array<Int>) {
    if (optionsText.size != optionsImages.size) throw Exception("Wrong count")

    val intent = Intent(this, AlertChooserActivity::class.java)
    intent.putExtra("TITLE", title)
    intent.putExtra("OPTIONS", optionsText)
    intent.putExtra("OPTIONS_IMAGES", optionsImages)
    startActivityForResult(intent, ALERT_OPTION_CHOSE)
}

class AlertChooserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_chooser)

        val titleText = intent.getStringExtra("TITLE")
        val optionText = intent.extras!!.getStringArray("OPTIONS")!!
        val optionImages = intent.extras!!.getStringArray("OPTIONS_IMAGES")

        title = titleText

        if (optionImages != null && optionImages.isNotEmpty()) {

        } else {
            optionText.forEachIndexed { index, option ->
                val v = layoutInflater.inflate(R.layout.activity_alert_chooser_text, mainGrid, false) as TextView

                v.text = option
                v.setOnClickListener {
                    val data = Intent()
                    data.putExtra(RETURNED_VALUE, option)
                    data.putExtra(RETURNED_INDEX, index)
                    setResult(RESULT_OK, data)
                    this@AlertChooserActivity.finish()
                }
                mainGrid.addView(v)
            }
        }
    }
}
