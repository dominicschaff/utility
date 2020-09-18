package zz.utility.utility

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_clock.*
import zz.utility.R
import zz.utility.helpers.FullscreenActivity
import zz.utility.helpers.toTimeFull
import java.util.*

class ClockActivity : FullscreenActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
    }
}