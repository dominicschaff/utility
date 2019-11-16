package zz.utility.browser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_reader.*
import zz.utility.R
import java.io.File

class ReaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        val path = File(intent.extras?.getString(PATH) ?: return)
        filename.text = path.absolutePath
        text.text = path.readText()
    }
}
