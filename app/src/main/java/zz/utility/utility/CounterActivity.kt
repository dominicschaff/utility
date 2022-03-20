package zz.utility.utility

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.SharedValues
import zz.utility.R
import zz.utility.databinding.ActivityCounterBinding
import zz.utility.helpers.ignore

class CounterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCounterBinding
    private val list = ArrayList<EditText>()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCounterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repeat(10) {
            val l = layoutInflater.inflate(R.layout.counter_row, binding.items, false)
            val e = l.findViewById<EditText>(R.id.content)
            list.add(e)
            e.setText("0")
            l.findViewById<ImageButton>(R.id.lower).setOnClickListener {
                e.setText("${e.text.toString().toDouble() - 0.25}")
                calculate()
            }
            l.findViewById<ImageButton>(R.id.raise).setOnClickListener {
                e.setText("${e.text.toString().toDouble() + 0.25}")
                calculate()
            }
            e.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    calculate()
                }

            })
            binding.items.addView(l)
        }

        binding.reset.setOnClickListener {
            binding.total.text = ""
            list.forEach {
                it.setText("0")
            }
        }
        binding.equals.setOnClickListener { calculate() }
    }

    private fun calculate() {
        {
            val total: Double = list.map { it.text.toString().toDouble() }.sum()
            binding.total.text = "$total"
        }.ignore()
    }
}