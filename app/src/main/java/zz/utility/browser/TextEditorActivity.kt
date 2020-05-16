package zz.utility.browser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.activity_text_editor.*
import zz.utility.R
import java.io.File

class TextEditorActivity : AppCompatActivity() {

    private lateinit var path: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)

        val markwon = Markwon.create(this);

        val editor = MarkwonEditor.create(markwon)

        text.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))

        path = File(intent.extras?.getString(PATH) ?: return)
        filename.text = path.absolutePath

        text.setText(path.readText())
    }

    override fun onPause() {
        super.onPause()
        path.writeText(text.text.toString())

    }
}
