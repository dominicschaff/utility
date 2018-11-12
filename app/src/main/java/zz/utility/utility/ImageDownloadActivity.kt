package zz.utility.utility

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_image_download.*
import zz.utility.MAIN_CONFIG
import zz.utility.R
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
        refresh.setOnClickListener { doRefresh() }
    }

    private fun doRefresh() {
        Ion.with(image as ImageView)
                .placeholder(R.drawable.ic_refresh)
                .error(R.drawable.ic_block)
                .load(obj.url)
    }
}
