package my.edu.tarc.okuappg11.recyclerview

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.AdminEventDetailsActivity
import my.edu.tarc.okuappg11.utils.GlideLoader

class EventsAdapter(private val eventCardArrayListList: ArrayList<EventCardArrayList>):RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_event_card_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventsAdapter.ViewHolder, position: Int) {
        val eventCardArrayListItem:EventCardArrayList = eventCardArrayListList[position]
        holder.itemTitle.text = eventCardArrayListItem.eventTitle
        holder.itemDescription.text = eventCardArrayListItem.eventDate
        holder.itemLocation.text = eventCardArrayListItem.eventLocation
        holder.itemTime.text = eventCardArrayListItem.eventTime
        if(eventCardArrayListItem.eventStatus == "pending"){
            holder.itemStatus.setTextColor(Color.WHITE)
            holder.itemStatus.text = "Pending"
        }else if (eventCardArrayListItem.eventStatus == "accepted"){
            holder.itemStatus.setTextColor(Color.GREEN)
            holder.itemStatus.text = "Accepted"

        }else if (eventCardArrayListItem.eventStatus == "rejected"){
            holder.itemStatus.setTextColor(Color.RED)
            holder.itemStatus.text = "Rejected"

        }

        Log.d("check",eventCardArrayListItem.eventStatus)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, AdminEventDetailsActivity::class.java)
            intent.putExtra("EventUID","${eventCardArrayListItem.eventId}")
            holder.itemView.context.startActivity(intent)
        }
        GlideLoader(holder.itemImage.context).loadUserPicture(Uri.parse(eventCardArrayListItem.imageUri),holder.itemImage)


    }

    override fun getItemCount(): Int {
        return eventCardArrayListList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemTitle : TextView
        var itemDescription : TextView
        var itemTime : TextView
        var itemLocation : TextView
        var itemStatus : TextView

        init{
            itemImage = itemView.findViewById(R.id.ivBookmark)
            itemTitle = itemView.findViewById(R.id.showTopicName)
            itemDescription = itemView.findViewById(R.id.showTopicDescription)
            itemTime = itemView.findViewById(R.id.showTime)
            itemLocation = itemView.findViewById(R.id.showLocation)
            itemStatus = itemView.findViewById(R.id.showStatus)




        }
    }
}