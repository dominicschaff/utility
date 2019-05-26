package zz.utility.poc

import android.os.Bundle
import zz.utility.R
import zz.utility.helpers.FullscreenActivity

class CarDashActivity : FullscreenActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_dash)
    }
}
