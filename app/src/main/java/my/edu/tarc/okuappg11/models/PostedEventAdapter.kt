package my.edu.tarc.okuappg11.models

import android.net.Uri
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.ViewEventOrganizeDetailsActivity
import my.edu.tarc.okuappg11.data.PostedEventArrayList
import my.edu.tarc.okuappg11.utils.GlideLoader

class PostedEventAdapter(private val postedEventList: ArrayList<PostedEventArrayList>) :
    RecyclerView.Adapter<PostedEventAdapter.ViewHolder>() {
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_posted_event, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postedEventArrayListItem: PostedEventArrayList = postedEventList[position]
        holder.eventId.text = postedEventArrayListItem.eventId.toString()
        holder.eventName.text = postedEventArrayListItem.eventName.toString()
        holder.eventDate.text = postedEventArrayListItem.startDate
        holder.eventTime.text = postedEventArrayListItem.startTime
        holder.eventLocation.text = postedEventArrayListItem.location.toString()
        if(postedEventArrayListItem.eventStatus == "pending"){
            holder.eventStatus.setTextColor(Color.WHITE)
            holder.eventStatus.text = "Pending"
        }else if (postedEventArrayListItem.eventStatus == "accepted"){
            holder.eventStatus.setTextColor(Color.GREEN)
            holder.eventStatus.text = "Accepted"

        }else if (postedEventArrayListItem.eventStatus == "rejected"){
            holder.eventStatus.setTextColor(Color.RED)
            holder.eventStatus.text = "Rejected"

        }
        GlideLoader(holder.ivBookmark.context).loadUserPicture(Uri.parse(postedEventArrayListItem.eventThumbnailURL),holder.ivBookmark)

        //val url: Uri? = Uri.parse(bookmarkArrayListItem.eventThumbnailURL)
        //this.context?.let { Glide.with(it).load(url).into(holder.eventThumbnailURL)}

        val eventId = holder.eventId.text

        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView.context, ViewEventOrganizeDetailsActivity::class.java)
            intent.putExtra("EventUID","${eventId.toString()}")
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return postedEventList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventId: TextView = itemView.findViewById(R.id.txtEventId)
        val eventName: TextView = itemView.findViewById(R.id.showTopicName)
        val eventDate: TextView = itemView.findViewById(R.id.showTopicDescription)
        val eventTime: TextView = itemView.findViewById(R.id.showTime)
        val eventLocation: TextView = itemView.findViewById(R.id.showLocation)
        val eventStatus: TextView = itemView.findViewById(R.id.showStatus)
        val ivBookmark: ImageView = itemView.findViewById(R.id.ivBookmark)

    }
}