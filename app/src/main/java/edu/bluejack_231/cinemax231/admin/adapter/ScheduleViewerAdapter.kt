import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.admin.adapter.TimeViewerAdapter
import edu.bluejack_231.cinemax231.model.Schedule
import java.sql.Time

class ScheduleViewerAdapter(private val dataSet: MutableList<Schedule>) :
    RecyclerView.Adapter<ScheduleViewerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View, time: ArrayList<String>) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val rcv: RecyclerView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.item_schedule_viewer_date)

            val adapter = TimeViewerAdapter(time)

            rcv = view.findViewById(R.id.item_schedule_rcv)
            rcv.layoutManager = GridLayoutManager(view.context, 3)
            rcv.adapter = adapter
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_schedule_viewer, viewGroup, false)

        return ViewHolder(view, dataSet[viewType].time)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].date
        Log.d("Inside", dataSet[position].time.toString())
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
