package zz.utility.utility

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_hacker_news.*
import kotlinx.android.synthetic.main.hacker_article.view.*
import zz.utility.R
import zz.utility.helpers.s


class HackerNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_news)
        getTop()
        swipe_to_refresh.setOnRefreshListener { getTop() }
    }

    fun getTop() {
        list.removeAllViews()
        Ion.with(this)
                .load("https://hacker-news.firebaseio.com/v0/topstories.json")
                .asJsonArray()
                .setCallback { _, result ->
                    swipe_to_refresh.isRefreshing = true
                    result.forEach {
                        getItem(it.asLong)
                    }
                    swipe_to_refresh.isRefreshing = false
                }
    }

    fun getItem(id: Long) {
        Ion.with(this)
                .load("https://hacker-news.firebaseio.com/v0/item/${id}.json?print=pretty")
                .asJsonObject()
                .setCallback { _, result ->
                    val l = layoutInflater.inflate(R.layout.hacker_article, list, false)
                    l.type.text = result.s("type")
                    l.title.text = result.s("title")
                    l.url.text = result.s("url")
                    l.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result.s("url")))
                        startActivity(browserIntent)
                    }
                    list.addView(l)
                }
    }
}
