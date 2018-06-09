package zz.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.CardView
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sensors.*
import java.util.*

class SensorsActivity : Activity(), SensorEventListener {

    private var sensors = HashMap<String, TextView>()
    private lateinit var mSensorManager: SensorManager
    private lateinit var deviceSensors: List<Sensor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val listSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        for (s in listSensors) {
            val cv = CardView(this, null, 0)
            val title = TextView(this)
            title.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )

            title.setTextAppearance(R.style.Text_Large)
            title.text = s.name
            val content = TextView(this)
            content.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            content.setTextAppearance(R.style.Text_Right_Mono)
            content.text = ""
            val ll = LinearLayout(this, null, 0)
            ll.orientation = LinearLayout.VERTICAL
            ll.addView(title)
            ll.addView(content)
            cv.addView(ll)

            sensors.put(s.name, content)
            list_sensors.addView(cv)
        }
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("DefaultLocale")
    override fun onSensorChanged(event: SensorEvent) {
        var s = String.format("%.2f", event.values[0])
        for (x in 1 until event.values.size)
            s = String.format("%s/%.2f", s, event.values[x])
        s = String.format("%s\n [A:%d | R:%.2f]", s, event.accuracy, event.sensor.resolution)
        val sensor = sensors[event.sensor.name]
        if (sensor != null) sensor.text = s
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    public override fun onResume() {
        super.onResume()
        for (s in this.deviceSensors)
            mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL)
    }

    public override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
