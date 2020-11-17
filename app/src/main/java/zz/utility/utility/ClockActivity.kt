package zz.utility.utility

import android.os.Bundle
import zz.utility.R
import zz.utility.helpers.FullscreenActivity

class ClockActivity : FullscreenActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
    }
}