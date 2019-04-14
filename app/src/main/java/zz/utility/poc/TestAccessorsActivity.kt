package zz.utility.poc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import zz.utility.R
import zz.utility.helpers.log

class TestAccessorsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_test_accessors)

        val x = ContextCompat.getExternalFilesDirs(this, null)

        x.forEach {
            it.absolutePath.log()
        }
    }
}
