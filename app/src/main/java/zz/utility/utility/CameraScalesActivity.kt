package zz.utility.utility

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera.*
import zz.utility.R

class CameraScalesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        grid.columnCount = if (resources.getBoolean(R.bool.is_landscape)) 6 else 3
    }
}
