package my.edu.tarc.okuappg11.models

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.EventDetailsActivity
import my.edu.tarc.okuappg11.data.VolunteerArrayList
import my.edu.tarc.okuappg11.utils.GlideLoader

class MyVolunteerEventAdapter(private val volunteerArrayList: ArrayList<VolunteerArrayList>) :
    RecyclerView.Adapter<MyVolunteerEventAdapter.ViewHolder>() {

    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_volunteer_event, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val volunteerArrayListItem: VolunteerArrayList = volunteerArrayList[position]
        holder.eventId.text = volunteerArrayListItem.eventId.toString()
        holder.eventName.text = volunteerArrayListItem.eventName.toString()
        holder.eventDate.text = volunteerArrayListItem.startDate
        holder.eventLocation.text = volunteerArrayListItem.location.toString()
        holder.eventTime.text = volunteerArrayListItem.startTime.toString()
        GlideLoader(holder.ivBookmark.context).loadUserPicture(Uri.parse(volunteerArrayListItem.eventThumbnailURL),holder.ivBookmark)

        //val url: Uri? = Uri.parse(bookmarkArrayListItem.eventThumbnailURL)
        //this.context?.let { Glide.with(it).load(url).into(holder.eventThumbnailURL)}

        val eventId = holder.eventId.text
        holder.itemView.setOnClickListener(){

            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
            intent.putExtra("EventUID","${eventId.toString()}")
            holder.itemView.context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return volunteerArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventId: TextView = itemView.findViewById(R.id.txtEventId)
        val eventName: TextView = itemView.findViewById(R.id.showTopicName)
        val eventDate: TextView = itemView.findViewById(R.id.showTopicDescription)
        val eventLocation: TextView = itemView.findViewById(R.id.showLocation)
        val eventTime: TextView = itemView.findViewById(R.id.showTime)
        val ivBookmark: ImageView = itemView.findViewById(R.id.ivBookmark)

    }
}