package my.edu.tarc.okuappg11.models

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_search.view.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.EventDetailsActivity

class SearchListAdapter(var searchList: ArrayList<SearchModel>): RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.SearchListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_search, parent, false)
        return SearchListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchListAdapter.SearchListViewHolder, position: Int) {
        val searchListItem: SearchModel = searchList[position]
        holder.name.text = searchListItem.eventName
        holder.id.text = searchListItem.eventID

        val eventIDNumber: String = searchListItem.eventID

        holder.itemView.setOnClickListener(){
            Toast.makeText(holder.itemView.context,"You clicked on item # ${position + 1}", Toast.LENGTH_SHORT).show()
            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
            intent.putExtra("EventUID", eventIDNumber)
            holder.itemView.context.startActivity(intent)
        }
    }

    class SearchListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        fun bind(searchModel: SearchModel){
//            itemView.tvNameSearch.text = searchModel.eventName
//        }
        val name : TextView = itemView.findViewById(R.id.tvNameSearch)
        val id : TextView = itemView.findViewById(R.id.tvIDSearch)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
