package zz.utility.utility.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import zz.utility.R
import zz.utility.databinding.ActivityItemDetailBinding

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private lateinit var itemTitle: String
    private lateinit var itemContent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            itemTitle = it.getString(ITEM_TITLE, "")
            itemContent = it.getString(ITEM_CONTENT, "")
            activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = itemTitle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        rootView.findViewById<TextView>(R.id.item_detail).text = itemContent

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ITEM_TITLE = "item_title"
        const val ITEM_CONTENT = "item_content"
    }
}
