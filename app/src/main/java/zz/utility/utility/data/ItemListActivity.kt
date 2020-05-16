package zz.utility.utility.data

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_item_list.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list_content.view.*
import zz.utility.R
import zz.utility.configFile
import zz.utility.helpers.a
import zz.utility.helpers.mapObject
import zz.utility.helpers.s

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (item_detail_container != null) twoPane = true

        setupRecyclerView(item_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val content = configFile().a("data")
        val items = content.mapObject { Item(s("title"), s("content")) }
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, items, twoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                                        private val values: List<Item>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.title.text = item.title

            with(holder.itemView) {
                tag = item
                setOnClickListener { v ->
                    if (twoPane) {
                        val fragment = ItemDetailFragment().apply {
                            arguments = Bundle().apply {
                                putString(ItemDetailFragment.ITEM_TITLE, item.title)
                                putString(ItemDetailFragment.ITEM_CONTENT, item.content)
                            }
                        }
                        parentActivity.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit()
                    } else {
                        val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                            putExtra(ItemDetailFragment.ITEM_TITLE, item.title)
                            putExtra(ItemDetailFragment.ITEM_CONTENT, item.content)
                        }
                        v.context.startActivity(intent)
                    }
                }
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.title
        }
    }
}

data class Item(
        val title: String,
        val content: String
)