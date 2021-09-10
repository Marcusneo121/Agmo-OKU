package my.edu.tarc.okuappg11.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.okuappg11.R

class EventsAdapter(private val eventCardArrayListList: ArrayList<EventCardArrayList>):RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.event_card_layout,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventsAdapter.ViewHolder, position: Int) {
        val eventCardArrayListItem:EventCardArrayList = eventCardArrayListList[position]
        holder.itemTitle.text = eventCardArrayListItem.eventTitle
        holder.itemDescription.text = eventCardArrayListItem.eventDescription
        holder.itemImage.setImageURI(eventCardArrayListItem.imageUri)

    }

    override fun getItemCount(): Int {
        return eventCardArrayListList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemTitle : TextView
        var itemDescription : TextView

        init{
            itemImage = itemView.findViewById(R.id.ivEventCardThumbnail)
            itemTitle = itemView.findViewById(R.id.tvEventTitle)
            itemDescription = itemView.findViewById(R.id.tvEventCardDescription)


        }
    }
}