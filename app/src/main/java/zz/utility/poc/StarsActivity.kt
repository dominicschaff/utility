package zz.utility.poc

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stars.*
import zz.utility.R
import zz.utility.helpers.FullscreenActivity

class StarsActivity : FullscreenActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars)
    }

    override fun onStart() {
        super.onStart()
        stars.onStart()
    }

    override fun onStop() {
        stars.onStop()
        super.onStop()
    }
}
