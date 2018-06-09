package zz.utility

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_random_code.*

class RandomCodeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_code)

        data.setOnClickListener { doData() }
        doData()
    }

    private fun doData() {

        val sb = StringBuilder()

        for (i in 0..36) {
            for (j in 0..41)
                sb.append((Math.random() * (126 - 33) + 33).toInt().toChar())
            sb.append("\n")
        }
        data.text = sb.toString()
    }
}
