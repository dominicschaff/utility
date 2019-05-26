package zz.utility.browser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text_view.*
import zz.utility.R
import java.io.File

class TextViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view)
        val path = File(intent.extras?.getString(PATH) ?: return)
        filename.text = path.absolutePath
        text.text = path.readText()
    }
}
