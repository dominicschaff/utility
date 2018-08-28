package zz.utility

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_quote_api.*
import zz.utility.helpers.o
import zz.utility.helpers.s

class QuoteApiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote_api)

        swipe_to_refresh.setOnRefreshListener { doRefresh() }
        doRefresh()
    }

    private fun doRefresh() {
        swipe_to_refresh.isRefreshing = true
        Ion.with(this)
                .load("http://quotesondesign.com/wp-json/posts?filter[orderby]=rand&filter[posts_per_page]=1")
                .asJsonArray()
                .setCallback { _, result ->
                    swipe_to_refresh.isRefreshing = false
                    result.o(0).apply {
                        quote.text = Html.fromHtml(s("content")
                                .replace("<p>", "")
                                .replace("</p>", "")
                                .trim(),
                                Html.FROM_HTML_MODE_LEGACY
                        )
                        author.text = s("title")
                    }
                }
    }
}
