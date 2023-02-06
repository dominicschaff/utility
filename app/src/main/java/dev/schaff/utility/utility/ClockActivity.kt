package dev.schaff.utility.utility

import android.os.Bundle
import dev.schaff.utility.R
import dev.schaff.utility.helpers.FullscreenActivity

class ClockActivity : FullscreenActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
    }
}