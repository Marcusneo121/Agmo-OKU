package my.edu.tarc.okuappg11.models

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.EventDetailsActivity
import my.edu.tarc.okuappg11.utils.GlideLoader

class TrendingAdapter(var trendingList: ArrayList<TrendingModel>): RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrendingAdapter.TrendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_home_trending, parent, false)
        return TrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrendingAdapter.TrendingViewHolder, position: Int) {
        val trendingListItem: TrendingModel = trendingList[position]
        holder.name.text = trendingListItem.eventName
        holder.id.text = trendingListItem.eventID
        holder.eventTrendingDate.text = trendingListItem.startDate
        GlideLoader(holder.ivTrending.context).loadUserPicture(Uri.parse(trendingListItem.eventThumbnailURL),holder.ivTrending)

        val eventIDNumber: String = trendingListItem.eventID

        holder.itemView.setOnClickListener(){
            //Toast.makeText(holder.itemView.context,"You clicked on item # ${position + 1}", Toast.LENGTH_SHORT).show()
            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
            intent.putExtra("EventUID", eventIDNumber)
            holder.itemView.context.startActivity(intent)
        }
    }

    class TrendingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.tvTrendingEvName)
        val id : TextView = itemView.findViewById(R.id.tvIDTrending)
        val eventTrendingDate: TextView = itemView.findViewById(R.id.tvTrendingDate)
        val ivTrending: ImageView = itemView.findViewById(R.id.ivTrending)
    }

    override fun getItemCount(): Int {
        return trendingList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}