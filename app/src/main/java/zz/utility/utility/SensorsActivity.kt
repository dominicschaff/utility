package zz.utility.utility

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sensors.*
import zz.utility.R
import java.util.*

class SensorsActivity : AppCompatActivity(), SensorEventListener {

    private var sensors = HashMap<String, TextView>()
    private lateinit var mSensorManager: SensorManager
    private lateinit var deviceSensors: List<Sensor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        deviceSensors.forEach {
            val cv = layoutInflater.inflate(R.layout.card_view, list_sensors, false)
            cv.findViewById<TextView>(R.id.heading).text = it.name

            sensors[it.name] = cv.findViewById(R.id.content)
            list_sensors.addView(cv)w
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onSensorChanged(event: SensorEvent) {
        var s = String.format("%.2f", event.values[0])
        for (x in 1 until event.values.size)
            s = String.format("%s/%.2f", s, event.values[x])
        sensors[event.sensor.name]?.text = "%s\n [A:%d | R:%.2f]".format(s, event.accuracy, event.sensor.resolution)
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    public override fun onResume() {
        super.onResume()
        deviceSensors.forEach { mSensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    public override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
