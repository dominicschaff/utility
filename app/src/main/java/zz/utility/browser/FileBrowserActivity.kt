package zz.utility.browser

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_file_browser.*
import zz.utility.R
import zz.utility.helpers.*
import zz.utility.views.ALERT_OPTION_CHOSE
import zz.utility.views.RETURNED_INDEX
import zz.utility.views.RETURNED_VALUE
import zz.utility.views.chooser
import java.io.File


class FileBrowserActivity : AppCompatActivity() {

    private lateinit var path: File
    private val files = ArrayList<File>()
    private val folders = ArrayList<File>()
    private lateinit var adapter: MyFileAdapter

    private var choosing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_browser)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_view.setHasFixedSize(true)

        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(applicationContext, if (resources.getBoolean(R.bool.is_landscape)) 4 else 2)
        recycler_view.layoutManager = layoutManager
        adapter = MyFileAdapter(this, files, folders)
        recycler_view.adapter = adapter
        swipe_to_refresh.setOnRefreshListener { refreshList() }


        val localPath = intent.extras?.getString(PATH)

        if (localPath == null) {
            choosing = true
            val x = ContextCompat.getExternalFilesDirs(this, null).map { getRootOfInnerSdCardFolder(it) }

            val list = ArrayList<File>()
            list.add(Environment.getExternalStorageDirectory())
            for (i in 1 until x.size) {
                if (x[i] != null) {
                    list.add(x[i]!!)
                }
            }

            chooser("Select Base Path", list.map { it.absolutePath }.toTypedArray())
        } else {
            path = File(localPath)
            if (path.isDirectory)
                getFileList()
            else alert("This is not a directory and shouldn't be opened")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ALERT_OPTION_CHOSE -> {
                if (resultCode == RESULT_OK) {
                    "Is this initial selection: ${if (choosing) "yes" else "no"}".error()
                    if (choosing) {
                        choosing = false
                        path = File(data!!.getStringExtra(RETURNED_VALUE))
                        if (path.isDirectory)
                            getFileList()
                    } else {
                        adapter.runAction(data!!.getIntExtra(RETURNED_INDEX, 0))
                        refreshList()
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getFileList() {
        title = path.name
        supportActionBar?.subtitle = path.absolutePath

        refreshList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = consume { menuInflater.inflate(R.menu.menu_folders, menu) }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_create_directory -> consume {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("New Directory Name")

            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                File(path, input.text.toString().trim()).apply {
                    if (exists()) alert("There already exists the same directory in here")
                    else {
                        mkdir()
                        refreshList()
                    }
                }

            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        }
        R.id.action_create_nomedia -> consume {
            File(path, ".nomedia").createNewFile()
            refreshList()
        }
        R.id.action_sort_folder -> consume {
            startActivity(Intent(this, SorterActivity::class.java).putExtra(PATH, path.absolutePath))
        }
        R.id.action_details -> consume {
            alert("Total file size is ${path.getFileSize().formatSize()}\nTotal Files: ${path.getFileCount()}")
        }
        android.R.id.home -> consume { finish() }
        else -> super.onOptionsItemSelected(item)
    }

    fun refreshList() {
        swipe_to_refresh.isRefreshing = true

        FileRefresh(path) { totalSize: Long, result: Array<File>?, foldersResult: Array<File>? ->
            swipe_to_refresh.isRefreshing = false
            files.clear()
            if (result == null || result.isEmpty()) {
                empty_directory.see()
                adapter.notifyDataSetChanged()
                return@FileRefresh
            }
            files.addAll(result)
            adapter.notifyDataSetChanged()
            empty_directory.unsee()
            folders.clear()
            if (foldersResult != null && foldersResult.isNotEmpty())
                folders.addAll(foldersResult)
            title = "${result.size}:${totalSize.formatSize()}"
        }.execute()
    }

    class FileRefresh(val path: File, val f: (Long, Array<File>?, Array<File>?) -> Unit) : AsyncTask<Void, Void, Array<File>>() {
        private var totalSize: Long = 0
        private val folders = ArrayList<File>()
        override fun doInBackground(vararg params: Void?): Array<File>? {
            val filesTemp: Array<File> = path.listFiles()

            filesTemp.sortWith(Comparator { o1, o2 ->
                when {
                    o1.isDirectory and !o2.isDirectory -> -1
                    o2.isDirectory and !o1.isDirectory -> 1
                    else -> o1.name.toLowerCase().compareTo(o2.name.toLowerCase())
                }
            })
            filesTemp.forEach {
                totalSize += if (it.isFile) it.length() else 0
                if (it.isDirectory) {
                    folders.add(it)
                }
            }

            return filesTemp
        }

        override fun onPostExecute(result: Array<File>?) = f(totalSize, result, folders.toTypedArray())
    }

    private fun getRootOfInnerSdCardFolder(file: File?): File? {
        var localFile: File? = file ?: return null
        val totalSpace = localFile!!.totalSpace
        while (true) {
            val parentFile = localFile!!.parentFile
            if (parentFile == null || parentFile.totalSpace != totalSpace)
                return localFile
            localFile = parentFile
        }
    }
}
