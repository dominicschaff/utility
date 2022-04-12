package zz.utility.utility

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.koushikdutta.ion.Ion
import com.koushikdutta.ion.ProgressCallback
import zz.utility.R
import zz.utility.databinding.ActivityLocalFileBinding
import zz.utility.externalFile
import zz.utility.helpers.formatSize
import zz.utility.helpers.toDateFull
import zz.utility.homeDir
import java.io.File
import java.util.*


class LocalFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocalFileBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ArrayAdapter.createFromResource(
            this,
            R.array.urls,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.downloadList.adapter = adapter
        }
        binding.btnDownloadList.setOnClickListener {
            val url = (binding.downloadList.selectedItem) as String
            val fileName = url.split("/").last()
            Ion.with(this)
                .load(url)
                .progressBar(binding.progress)
                .progress(ProgressCallback { downloaded, total -> print("$downloaded / $total") })
                .write(externalFile(fileName))
        }
        binding.btnDownloadLink.setOnClickListener {
            val url = binding.downloadLink.text.toString()
            val fileName = url.split("/").last()
            Ion.with(this)
                .load(url)
                .progressBar(binding.progress)
                .progress(ProgressCallback { downloaded, total -> print("$downloaded / $total") })
                .write(externalFile(fileName))
        }
        val files = ArrayList<File>()
        homeDir().listFiles()?.forEach {
            if (it.isFile) {
                files.add(it)
            }
        }

        files.forEach {
            val modified = Date(it.lastModified())
            val tv= TextView(this)
            tv.text = "${it.name} - ${it.length().formatSize()} - ${modified.toDateFull()}"
            binding.localFiles.addView(tv)
        }
    }
}