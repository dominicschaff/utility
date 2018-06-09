package zz.utility.poc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_welcome.*
import zz.utility.R
import zz.utility.helpers.consume
import zz.utility.helpers.see
import zz.utility.helpers.unsee

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        key_1.setOnClickListener { 1.a() }
        key_2.setOnClickListener { 2.a() }
        key_3.setOnClickListener { 3.a() }
        key_4.setOnClickListener { 4.a() }
        key_5.setOnClickListener { 5.a() }
        key_6.setOnClickListener { 6.a() }
        key_7.setOnClickListener { 7.a() }
        key_8.setOnClickListener { 8.a() }
        key_9.setOnClickListener { 9.a() }
        key_0.setOnClickListener { 0.a() }
        key_back.setOnClickListener { backOne() }
        key_go.setOnClickListener { goFind() }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_1 or KeyEvent.KEYCODE_NUMPAD_1 -> consume { 1.a() }
            KeyEvent.KEYCODE_2 or KeyEvent.KEYCODE_NUMPAD_2 -> consume { 2.a() }
            KeyEvent.KEYCODE_3 or KeyEvent.KEYCODE_NUMPAD_3 -> consume { 3.a() }
            KeyEvent.KEYCODE_4 or KeyEvent.KEYCODE_NUMPAD_4 -> consume { 4.a() }
            KeyEvent.KEYCODE_5 or KeyEvent.KEYCODE_NUMPAD_5 -> consume { 5.a() }
            KeyEvent.KEYCODE_6 or KeyEvent.KEYCODE_NUMPAD_6 -> consume { 6.a() }
            KeyEvent.KEYCODE_7 or KeyEvent.KEYCODE_NUMPAD_7 -> consume { 7.a() }
            KeyEvent.KEYCODE_8 or KeyEvent.KEYCODE_NUMPAD_8 -> consume { 8.a() }
            KeyEvent.KEYCODE_9 or KeyEvent.KEYCODE_NUMPAD_9 -> consume { 9.a() }
            KeyEvent.KEYCODE_0 or KeyEvent.KEYCODE_NUMPAD_0 -> consume { 0.a() }
            KeyEvent.KEYCODE_DEL or KeyEvent.KEYCODE_FORWARD_DEL -> consume { backOne() }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Int.a() {
        info_view.append("$this")
    }

    private fun backOne() {
        info_view.text = info_view.text.subSequence(0, info_view.text.length - 1)
    }

    private fun goFind() {
        welcome_wait.see()
        Thread({
            Thread.sleep(2000)
            runOnUiThread {
                welcome_wait.unsee()
                info_view.text = "Thank you please have a seat"
                Thread({
                    Thread.sleep(2000)
                    runOnUiThread {
                        info_view.text = ""
                    }
                }).start()
            }
        }).start()
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
