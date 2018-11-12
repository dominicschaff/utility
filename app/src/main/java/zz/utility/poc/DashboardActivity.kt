package zz.utility.poc

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_dashboard.*
import zz.utility.R

class DashboardActivity : Activity() {

    private val metrics = ArrayList<DashboardMetric>()
    private val adapter = MyMetricAdapter(metrics)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(applicationContext, if (resources.getBoolean(R.bool.is_landscape)) 4 else 2)
        recycler_view.layoutManager = layoutManager

        swipe_to_refresh.setOnRefreshListener { createMetrics() }

        recycler_view.adapter = adapter

        createMetrics()
    }

    private fun createMetrics() {
        swipe_to_refresh.isRefreshing = true
        val items = (Math.random() * 15 + 5).toInt()
        metrics.clear()
        for (i in 0..items) {
            metrics.add(DashboardMetric("Metric$i", (Math.random() * 100).toInt()))
        }
        adapter.notifyDataSetChanged()
        swipe_to_refresh.isRefreshing = false
    }
}

data class DashboardMetric(val name: String, val metric: Int)

class DashboardTile(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val metric: TextView = view.findViewById(R.id.metric)
    val name: TextView = view.findViewById(R.id.name)
}

class MyMetricAdapter(private val metrics: ArrayList<DashboardMetric>) : androidx.recyclerview.widget.RecyclerView.Adapter<DashboardTile>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DashboardTile =
            DashboardTile(LayoutInflater.from(viewGroup.context).inflate(R.layout.dashboard_tile, viewGroup, false))

    override fun onBindViewHolder(viewHolder: DashboardTile, i: Int) {
        val f = metrics[i]

        viewHolder.name.text = f.name
        viewHolder.metric.text = f.metric.toString()
    }

    override fun getItemCount(): Int = metrics.size

}