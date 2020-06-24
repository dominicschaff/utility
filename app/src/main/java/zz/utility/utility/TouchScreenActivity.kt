package zz.utility.utility

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_touch_screen.*
import zz.utility.R
import zz.utility.helpers.screenshot
import zz.utility.helpers.toDateFile
import zz.utility.homeDir
import java.io.File
import java.util.*

class TouchScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_screen)

        fab_screenshot.setOnClickListener {
            val notes = File(homeDir(), "notes")
            if (!notes.exists()) notes.mkdir()
            draw_area.screenshot(File(notes, "/${Date().toDateFile()}.jpeg"))
        }
        fab_clear.setOnClickListener {
            draw_area.clearSpace()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}
