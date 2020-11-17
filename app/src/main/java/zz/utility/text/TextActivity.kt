package zz.utility.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text.*
import zz.utility.R
import zz.utility.externalFile
import zz.utility.helpers.toast

class TextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        action_copy_all.setOnClickListener {
            val file = externalFile("shared.json")
            val text = file.readText()
            copyToClipboard(text)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(text, text))
        toast("Data copied to clipboard")
    }
}