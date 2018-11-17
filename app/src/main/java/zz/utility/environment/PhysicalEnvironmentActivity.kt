package zz.utility.environment

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_physical_environment.*
import zz.utility.R

class PhysicalEnvironmentActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var soundMeter: SoundMeter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_physical_environment)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        soundMeter = SoundMeter()
    }

    @SuppressLint("DefaultLocale")
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.name) {
            lightSensor.name -> {
                light_value.text = String.format("%.0f lux", event.values[0])
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getSoundValue() {
        Handler().postDelayed({
            if (soundMeter.ar != null) {
                val a = soundMeter.amplitude
                sound_value.text = "$a\n${(20 * Math.log10(a / 0.447)).toInt()} dB"
                getSoundValue()
            }
        }, 100)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    public override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

        soundMeter.start()
        getSoundValue()
    }

    public override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
        soundMeter.stop()
    }
}
