package my.edu.tarc.okuappg11.activities

import android.net.Uri
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.utils.GlideLoader

class AllUpcomingEventsAdapter(private val allUpcomingEventsList: ArrayList<AllUpcomingEventsArrayList>) :
    RecyclerView.Adapter<AllUpcomingEventsAdapter.ViewHolder>() {
    // private var _binding: Fragment? = null
    // private val binding get() = _binding!!
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore
    private var eventId:String? = null




    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllUpcomingEventsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_all_upcoming_events, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AllUpcomingEventsAdapter.ViewHolder, position: Int) {
        fStore = FirebaseFirestore.getInstance()

        val allUpcomingEventsArrayListItem: AllUpcomingEventsArrayList = allUpcomingEventsList[position]
        holder.eventName.text = allUpcomingEventsArrayListItem.eventName.toString()
        holder.eventDate.text = allUpcomingEventsArrayListItem.startDate
        holder.eventTime.text = allUpcomingEventsArrayListItem.startTime
        holder.eventLocation.text = allUpcomingEventsArrayListItem.location.toString()
        GlideLoader(holder.ivAllUpcomingEvents.context).loadUserPicture(Uri.parse(allUpcomingEventsArrayListItem.eventThumbnailURL),holder.ivAllUpcomingEvents)


        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView.context, QuitEventActivity::class.java)
            intent.putExtra("EventUID","${allUpcomingEventsArrayListItem.eventID.toString()}")
            holder.itemView.context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return allUpcomingEventsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.showTopicName)
        val eventDate: TextView = itemView.findViewById(R.id.showTopicDescription)
        val eventTime: TextView = itemView.findViewById(R.id.showTime)
        val eventLocation: TextView = itemView.findViewById(R.id.showLocation)
        val ivAllUpcomingEvents: ImageView = itemView.findViewById(R.id.ivAllUpcomingEvents)

    }



}