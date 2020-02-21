package zz.utility.browser.sound

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_sound_sorter.*
import zz.utility.R
import zz.utility.browser.PATH
import zz.utility.browser.moveToBin
import zz.utility.helpers.show
import zz.utility.helpers.toast
import zz.utility.helpers.hide
import zz.utility.isMusic
import java.io.File


class SoundSorterActivity : AppCompatActivity() {
    private lateinit var path: File
    private val files = ArrayList<File>()
    private lateinit var adapter: MyFileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_sorter)
        path = File(intent.extras?.getString(PATH) ?: "/1")
        if (!path.exists()) {
            finish()
            return
        }
        title = path.absolutePath
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        adapter = MyFileAdapter(this, files)
        recycler_view.adapter = adapter
        swipe_to_refresh.setOnRefreshListener { refreshList() }
        refreshList()
    }

    fun refreshList() {
        swipe_to_refresh.isRefreshing = true

        FileRefresh(path) { result: Array<File>? ->
            swipe_to_refresh.isRefreshing = false
            files.clear()
            if (result == null || result.isEmpty()) {
                empty_directory.show()
                adapter.notifyDataSetChanged()
                return@FileRefresh
            }
            files.addAll(result)
            adapter.notifyDataSetChanged()
            empty_directory.hide()
        }.execute()
    }

    class FileRefresh(val path: File, val f: (Array<File>?) -> Unit) : AsyncTask<Void, Void, Array<File>>() {
        override fun doInBackground(vararg params: Void?): Array<File>? {
            val filesTemp: Array<File> = path.listFiles()!!.filter { it.isMusic() }.toTypedArray()

            filesTemp.sortWith(Comparator { o1, o2 ->
                o1.name.toLowerCase().compareTo(o2.name.toLowerCase())
            })

            return filesTemp
        }

        override fun onPostExecute(result: Array<File>?) = f(result)
    }
}


class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.title)
    val play: ImageButton = view.findViewById(R.id.btn_play)
    val delete: ImageButton = view.findViewById(R.id.btn_delete)
}

class MyFileAdapter(private val activity: SoundSorterActivity, private val fileList: ArrayList<File>) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.audio_file, viewGroup, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.title.text = fileList[i].name
        viewHolder.delete.setOnClickListener {
            if (fileList[i].moveToBin()) activity.refreshList()
            else activity.toast("File could not be moved")
        }
        viewHolder.play.setOnClickListener {
            val mp: MediaPlayer = MediaPlayer.create(activity, fileList[i].toUri())
            mp.start()
        }
    }

    override fun getItemCount(): Int = fileList.size
}