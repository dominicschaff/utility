package zz.utility.utility

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import zz.utility.R
import zz.utility.databinding.ActivityLocalFileBinding
import zz.utility.databinding.ActivityScanningBinding
import com.koushikdutta.async.future.FutureCallback

import com.koushikdutta.ion.ProgressCallback

import com.koushikdutta.ion.Ion
import org.mapsforge.poi.android.storage.AndroidPoiPersistenceManagerFactory
import org.oscim.tiling.source.mapfile.MapFileTileSource
import zz.utility.externalFile
import zz.utility.homeDir
import java.io.File


class LocalFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocalFileBinding
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
    }
}