package zz.utility.browser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_text_view.*
import zz.utility.R
import zz.utility.isMarkdown
import java.io.File

class TextViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view)
        val path = File(intent.extras?.getString(PATH) ?: return)
        filename.text = path.absolutePath

        if (path.isMarkdown()) {
            val markwon = Markwon.create(this);
            markwon.setMarkdown(text, path.readText());
        } else {
            text.text = path.readText()
        }
    }
}
