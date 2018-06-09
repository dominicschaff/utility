package zz.utility

import android.app.Activity
import android.os.Bundle
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_quote.*
import zz.utility.helpers.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

data class Quote(val quote: String, val author: String)

class QuoteActivity : Activity() {
    private val quotes = ArrayList<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        doAsync({
            val quotes = ArrayList<Quote>()
            tryIgnore {

                val f = File("$HOME/quotes.json")
                if (!f.exists()) {
                    val json = JsonArray()
                    json.add(
                            JsonObject()
                                    .add("quote", "There are no quotes stored, update the file: $HOME/quotes.json")
                                    .add("author", "This application")
                    )
                    var stream: FileOutputStream? = null
                    try {
                        stream = FileOutputStream(f, true)
                        stream.write(json.toString().toByteArray())
                        stream.flush()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            if (stream != null) stream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }



                val gson = f.asJsonArray()
                gson.forEach {
                    val quote = it.asJsonObject
                    quotes.add(Quote(quote.s("quote"), quote.s("author")))
                }
            }
            return@doAsync quotes
        }, {
            quotes.addAll(it!!)
            doRefresh()
        })

        swipe_to_refresh.setOnRefreshListener({ doRefresh() })
    }

    private fun doRefresh() {
        swipe_to_refresh.isRefreshing = true
        val quoteObj = quotes.random()
        quote.text = quoteObj.quote
        author.text = quoteObj.author
        swipe_to_refresh.isRefreshing = false
    }
}
