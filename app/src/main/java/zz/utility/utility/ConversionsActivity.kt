package zz.utility.utility

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_conversions.*
import zz.utility.R

class ConversionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversions)

        conversion_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                doConversion(conversion_type.selectedItemPosition, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        doConversion(conversion_type.selectedItemPosition, conversion_value.progress)
        conversion_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                doConversion(position, conversion_value.progress)
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun doConversion(type: Int, input: Int) {
        converted_values.text = when (type) {
            0 -> { // distance mm
                val i = (input + 10000) / 100.0
                picked_value.text = "%.2f mm".format(i)
                """
                    %20.4f cm
                    %20.4f inch
                """.trimIndent().format(i / 10, i / 25.4)
            }
            1 -> { // distance m
                val i = (input + 10000) / 20.0
                picked_value.text = "%.1f m".format(i)
                """
                    %20.4f feet
                    %20.4f yard
                """.trimIndent().format(i * 3.281, i * 1.094)
            }
            2 -> { // distance km
                val i = (input + 10000) / 10.0
                picked_value.text = "%.0f km".format(i)
                """
                    %20.4f yards
                    %20.4f nautical miles
                    %20.4f miles
                """.trimIndent().format(i * 1094, i / 1.852, i / 1.609)
            }
            3 -> { // area m2
                val i = (input + 10000) * 1.0
                picked_value.text = "%.0f m²".format(i)
                """
                    %20.4f km²
                    %20.4f hectare
                    %20.4f acre
                """.trimIndent().format(i / 1e+6, i / 10000, i / 4047)
            }
            4 -> { // area km2
                val i = (input + 10000) * 1.0
                picked_value.text = "%.0f km²".format(i)
                """
                    %20.4f hectare
                    %20.4f acre
                """.trimIndent().format(i * 100, i * 247)
            }
            5 -> { // volume l
                val i = (input + 10000) / 20.0
                picked_value.text = "%.1f l".format(i)
                """
                    %20.4f imperial gallon
                    %20.4f imperial cup
                    %20.4f m3
                """.trimIndent().format(i / 4.546, i / 3.52, i / 1000)
            }
            6 -> { // weight g
                val i = (input + 10000) / 20.0
                picked_value.text = "%.1f g".format(i)
                """
                    %20.4f ounce
                    %20.4f pound
                """.trimIndent().format(i / 28.35, i / 454)
            }
            7 -> { // weight kg
                val i = (input + 10000) / 100.0
                picked_value.text = "%.0f kg".format(i)
                """
                    %20.4f pound
                    %20.4f stone
                """.trimIndent().format(i * 2.205, i / 6.35)
            }
            8 -> { // temperate c
                val i = (input + 700) / 30.0
                picked_value.text = "%.1f C°".format(i)
                """
                    %20.4f F°
                    %20.4f K°
                """.trimIndent().format((i * 9 / 5) + 32, i + 273.15)
            }
            else -> "No Type Picked"
        }
    }
}
