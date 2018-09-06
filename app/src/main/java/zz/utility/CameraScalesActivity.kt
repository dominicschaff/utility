package zz.utility

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera.*

class CameraScalesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        grid.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 6 else 3
    }
}
