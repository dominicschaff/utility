package zz.utility.utility

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import zz.utility.HOME
import zz.utility.R
import zz.utility.browser.sortFiles
import zz.utility.helpers.*
import java.io.File

data class Entry(val text: String, val author: String, val source: String)

class ListActivity : AppCompatActivity() {

    private val files = ArrayList<File>()

    private val items = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val p = File("$HOME/lists")
        if (!p.exists()) {
            alert("List directory")
            return
        }

        val f = File("$HOME/lists").listFiles()
        files.addAll(f)
        files.sortFiles()
        val fileNames: Array<String> = files.map { it.nameWithoutExtension }.toTypedArray()
        createChooser("Select file to run", fileNames, DialogInterface.OnClickListener { _, which ->
            supportActionBar?.subtitle = files[which].name
            doAsync({
                return@doAsync { files[which].asJsonArray().mapObject { Entry(s("text"), s("author"), s("source")) } }.or { ArrayList() }
            }, {
                items.addAll(it!!)
                doRefresh()
            })
        })
        swipe_to_refresh.setOnRefreshListener { doRefresh() }
    }

    private fun doRefresh() {
        swipe_to_refresh.isRefreshing = true
        if (items.isNotEmpty())
            items.random().apply {
                text_text.text = text
                text_author.text = author
                text_source.text = source
            }
        swipe_to_refresh.isRefreshing = false
    }
}
