package zz.utility

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_daily_comic.*


data class Xkcd(
        val month: String,
        val num: Long,
        val link: String,
        val year: String,
        val news: String,
        val safe_title: String,
        val transcript: String,
        val alt: String,
        val img: String,
        val title: String,
        val day: String
)

class DailyComicActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_comic)

        title = "Loading..."

        Ion.with(this)
                .load("https://xkcd.com/info.0.json")
                .`as`(object : TypeToken<Xkcd?>() {})
                .setCallback { _, xkcd ->
                    xkcd ?: return@setCallback
                    title = xkcd.title
                    text.text = xkcd.alt
                    supportActionBar?.subtitle = "${xkcd.day}/${xkcd.month}/${xkcd.year}"
                    Ion.with(image)
                            .placeholder(R.drawable.ic_refresh)
                            .error(R.drawable.ic_block)
                            .load(xkcd.img)
                }
    }
}
