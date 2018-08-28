package zz.utility.browser

import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_file_browser.*
import zz.utility.R
import zz.utility.helpers.consume
import zz.utility.helpers.formatSize
import zz.utility.helpers.see
import zz.utility.helpers.unsee
import java.io.File

class FileBrowserActivity : AppCompatActivity() {

    private lateinit var path: File
    private val files = ArrayList<File>()
    private val folders = ArrayList<File>()
    private lateinit var adapter: MyFileAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_browser)

        path = File(intent.extras?.getString("file_path")
                ?: Environment.getExternalStorageDirectory().absolutePath)

        title = path.name
        supportActionBar?.subtitle = path.absolutePath
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_view.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(applicationContext, if (resources.getBoolean(R.bool.is_landscape)) 4 else 2)
        recycler_view.layoutManager = layoutManager
        adapter = MyFileAdapter(this, files, folders)
        recycler_view.adapter = adapter
        refreshList()
        swipe_to_refresh.setOnRefreshListener { refreshList() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = consume { menuInflater.inflate(R.menu.menu_folders, menu) }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_create_directory -> consume {}
        R.id.action_create_nomedia -> consume {
            File(path, ".nomedia").createNewFile()
            refreshList()
        }
        android.R.id.home -> consume { finish() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun refreshList() {
        swipe_to_refresh.isRefreshing = true

        FileRefresh(path) { totalSize: Long, result: Array<File>?, foldersResult: Array<File>? ->
            swipe_to_refresh.isRefreshing = false
            if (result == null || result.isEmpty()) {
                empty_directory.see()
                return@FileRefresh
            }
            empty_directory.unsee()
            files.clear()
            files.addAll(result)
            folders.clear()
            folders.add(path.parentFile)
            if (foldersResult != null && foldersResult.isNotEmpty())
                folders.addAll(foldersResult)
            adapter.notifyDataSetChanged()
            title = "${path.name} [${result.size} : ${totalSize.formatSize()}]"
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
}
