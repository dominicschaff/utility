package zz.utility.utility

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sensors.*
import zz.utility.R
import java.util.*

class SensorsActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private val deviceSensors = ArrayList<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        deviceSensors.addAll(
                arrayOf(27,
                        Sensor.TYPE_LIGHT,
                        Sensor.TYPE_PROXIMITY,
                        Sensor.TYPE_GRAVITY,
                        Sensor.TYPE_LINEAR_ACCELERATION,
                        Sensor.TYPE_MAGNETIC_FIELD,
                        Sensor.TYPE_GYROSCOPE
                ).map { mSensorManager.getDefaultSensor(it) })
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            27 -> orientation.text = when (event.values[0].toInt()) {
                0 -> "Normal"
                1 -> "Left"
                2 -> "Upside Down"
                3 -> "Right"
                else -> "Flat | Unknown"
            }
            Sensor.TYPE_LIGHT -> light.text = "%.0f".format(event.values[0])
            Sensor.TYPE_PROXIMITY -> proximity.text = "%s : %.0f".format(if (event.values[0] > 0) "Away" else "Close", event.values[0])
            Sensor.TYPE_GRAVITY -> gravity.text = "Gravity\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_LINEAR_ACCELERATION -> acceleration.text = "Acceleration\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_MAGNETIC_FIELD -> magnetic.text = "Magnetic\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])
            Sensor.TYPE_GYROSCOPE -> gyroscope.text = "Gyroscope\nX : %.2f\nY : %.2f\nZ : %.2f".format(event.values[0], event.values[1], event.values[2])

        }
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
