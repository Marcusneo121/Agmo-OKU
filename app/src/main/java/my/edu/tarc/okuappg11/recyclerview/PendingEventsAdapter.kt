package my.edu.tarc.okuappg11.recyclerview

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.utils.GlideLoader

class PendingEventsAdapter(private val pendingEventCardArrayListList: ArrayList<PendingEventCardArrayList>):RecyclerView.Adapter<PendingEventsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingEventsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_event_card_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: PendingEventsAdapter.ViewHolder, position: Int) {
        val eventCardArrayListItem:PendingEventCardArrayList = pendingEventCardArrayListList[position]
        holder.itemTitle.text = eventCardArrayListItem.eventTitle
        holder.itemDescription.text = eventCardArrayListItem.eventDescription
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, AdminEventDetailsActivity::class.java)
            intent.putExtra("EventUID","${eventCardArrayListItem.eventId}")
            intent.putExtra("EventType", "pending")
            holder.itemView.context.startActivity(intent)
        }
        GlideLoader(holder.itemImage.context).loadUserPicture(Uri.parse(eventCardArrayListItem.imageUri),holder.itemImage)

    }

    override fun getItemCount(): Int {
        return pendingEventCardArrayListList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemTitle : TextView
        var itemDescription : TextView
        var itemTime : TextView
        var itemLocation : TextView


        init{
            itemImage = itemView.findViewById(R.id.ivBookmark)
            itemTitle = itemView.findViewById(R.id.showTopicName)
            itemDescription = itemView.findViewById(R.id.showTopicDescription)
            itemTime = itemView.findViewById(R.id.showTime)
            itemLocation = itemView.findViewById(R.id.showLocation)




        }
    }


}