package zz.utility.browser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text_view.*
import zz.utility.R
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


class TextViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view)
        val path = File(intent.extras?.getString(PATH) ?: return)
        title = path.absolutePath

        //Read text from file
        val sb = StringBuilder()

        try {
            val br = BufferedReader(FileReader(path))
            var line: String? = br.readLine()

            while (line != null) {
                sb.append(line).append('\n')
                line = br.readLine()
            }
            br.close()
        } catch (e: IOException) {
            //You'll need to add proper error handling here
        }

        text.setText(sb.toString())
    }
}
