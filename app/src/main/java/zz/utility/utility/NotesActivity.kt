package zz.utility.utility

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zz.utility.configFile
import zz.utility.databinding.ActivityNotesBinding
import zz.utility.externalFile
import zz.utility.helpers.ignore

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root);
        {
            binding.text.setText(externalFile("utility.json").readText())
        }.ignore()
        binding.save.setOnClickListener {
            externalFile("utility.json").writeText(binding.text.text.toString())
        }
    }
}