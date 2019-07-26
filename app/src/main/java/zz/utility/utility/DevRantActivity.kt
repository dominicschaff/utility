package zz.utility.utility

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_dev_rant.*
import zz.utility.R
import zz.utility.helpers.l
import zz.utility.helpers.o
import zz.utility.helpers.s
import zz.utility.helpers.toast

class DevRantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_rant)

        swipe_to_refresh.setOnRefreshListener { doRefresh() }
        doRefresh()
    }

    private fun doRefresh() {
        swipe_to_refresh.isRefreshing = true
        Ion.with(this)
                .load("https://devrant.com/api/devrant/rants/surprise?app=3")
                .asJsonObject()
                .setCallback { _, result ->
                    swipe_to_refresh.isRefreshing = false
                    result.o("rant").apply {
                        text.text = s("text")
                        author.text = " - ${s("user_username")}"
                        url.text="ID: ${l("id")}"
                    }
                }
    }
}
