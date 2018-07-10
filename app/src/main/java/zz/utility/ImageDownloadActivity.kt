package zz.utility

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_image_download.*
import zz.utility.helpers.a
import zz.utility.helpers.createChooser
import zz.utility.helpers.mapObject
import zz.utility.helpers.s

data class ImageLink(val title: String, val url: String)

class ImageDownloadActivity : AppCompatActivity() {

    private lateinit var obj: ImageLink

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_download)

        val s = MAIN_CONFIG.a("imageUrls").mapObject { ImageLink(s("title"), s("url")) }

        createChooser("Select file to run", s.map { it.title }.toTypedArray(), DialogInterface.OnClickListener { _, which ->
            Log.e("Thing", "choice")
            obj = s[which]
            title = obj.title
            doRefresh()
        })
        swipe_to_refresh.setOnRefreshListener { doRefresh() }
    }

    private fun doRefresh() {
        swipe_to_refresh.isRefreshing = true
        Log.e("Thing", obj.url)
        Ion.with(image).load(obj.url).then(FutureCallback { _, _ ->
            swipe_to_refresh.isRefreshing = false
        })
    }
}
