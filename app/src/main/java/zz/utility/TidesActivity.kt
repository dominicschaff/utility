package zz.utility

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_tides.*

class TidesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tides)
        Ion.with(image).load("https://www.tide-forecast.com/tides/Cape-Town-South-Africa.png")
    }
}
