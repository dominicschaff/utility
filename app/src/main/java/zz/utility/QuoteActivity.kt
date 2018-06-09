package zz.utility

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_quote.*
import zz.utility.helpers.*

data class Quote(val quote: String, val author: String)

class QuoteActivity : Activity() {
    private val quotes = ArrayList<Quote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        doAsync({
            val quotes = ArrayList<Quote>()
            tryIgnore {
                val gson = "$HOME/quotes.json".fileAsJsonArray()
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
