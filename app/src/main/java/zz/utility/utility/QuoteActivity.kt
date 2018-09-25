package zz.utility.utility

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_quote.*
import zz.utility.HOME
import zz.utility.R
import zz.utility.helpers.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

data class Quote(val quote: String, val author: String)

class QuoteActivity : AppCompatActivity() {
    private val quotes = ArrayList<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        doAsync({
            return@doAsync {

                val f = File("$HOME/quotes.json")
                if (!f.exists()) {
                    val json = JsonArray()
                    json.add(
                            JsonObject().apply {
                                addProperty("quote", "There are no quotes stored, update the file: $HOME/quotes.json")
                                addProperty("author", "This application")
                            }
                    )
                    var stream: FileOutputStream? = null
                    try {
                        stream = FileOutputStream(f, true)
                        stream.write(json.toString().toByteArray())
                        stream.flush()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        { stream?.close() }.ignore()
                    }
                }
                f.asJsonArray().mapObject { Quote(s("quote"), s("author")) }
            }.ignore(ArrayList())
        }, {
            quotes.addAll(it!!)
            doRefresh()
        })

        swipe_to_refresh.setOnRefreshListener { doRefresh() }
    }

    private fun doRefresh() {
        swipe_to_refresh.isRefreshing = true
        val quoteObj = quotes.random()
        quote.text = quoteObj.quote
        author.text = quoteObj.author
        swipe_to_refresh.isRefreshing = false
    }
}
