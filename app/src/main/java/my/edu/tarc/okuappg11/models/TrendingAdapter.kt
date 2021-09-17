package my.edu.tarc.okuappg11.models

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.activities.EventDetailsActivity
import my.edu.tarc.okuappg11.activities.QuitEventActivity
import my.edu.tarc.okuappg11.utils.GlideLoader

class TrendingAdapter(var trendingList: ArrayList<TrendingModel>): RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>()  {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private var eventID: String? = null

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
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        eventID = holder.id.text.toString()
        userID = fAuth.currentUser!!.uid

        holder.itemView.setOnClickListener(){
            //Toast.makeText(holder.itemView.context,"You clicked on item # ${position + 1}", Toast.LENGTH_SHORT).show()
//            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
//            intent.putExtra("EventUID", eventIDNumber)
//            holder.itemView.context.startActivity(intent)

            val jRef = fStore.collection("users").document(userID!!).collection("upcomingEvents").document(eventIDNumber)
            jRef.get()
                .addOnSuccessListener { document ->
                    if (document.getString("eventUID") == eventIDNumber) {

                        val intent =
                            Intent(holder.itemView.context, QuitEventActivity::class.java)
                        intent.putExtra("EventUID", "${eventIDNumber}")

                        holder.itemView.context.startActivity(intent)
                    }else{
                        val intent =
                            Intent(holder.itemView.context, EventDetailsActivity::class.java)
                        intent.putExtra("EventUID", "${eventIDNumber}")

                        holder.itemView.context.startActivity(intent)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
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