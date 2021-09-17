package my.edu.tarc.okuappg11.activities

import android.net.Uri
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.utils.GlideLoader

class BookmarkAdapter(private val bookmarkList: ArrayList<BookmarkArrayList>) :
    RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
   // private var _binding: Fragment? = null
   // private val binding get() = _binding!!

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var userID: String? = null
    private var eventID: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarkAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_bookmark, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookmarkAdapter.ViewHolder, position: Int) {
        fStore = FirebaseFirestore.getInstance()

        val bookmarkArrayListItem: BookmarkArrayList = bookmarkList[position]
        holder.eventName.text = bookmarkArrayListItem.eventName.toString()
        holder.eventDate.text = bookmarkArrayListItem.startDate
        holder.eventTime.text = bookmarkArrayListItem.startTime
        holder.eventID.text = bookmarkArrayListItem.eventID
        holder.eventLocation.text = bookmarkArrayListItem.location.toString()
        GlideLoader(holder.ivBookmark.context).loadUserPicture(Uri.parse(bookmarkArrayListItem.eventThumbnailURL),holder.ivBookmark)

        val eventIDNumber: String? = bookmarkArrayListItem.eventID
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        eventID = holder.eventID.text.toString()
        userID = fAuth.currentUser!!.uid

        holder.itemView.setOnClickListener(){
//            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
//            intent.putExtra("EventUID","${bookmarkArrayListItem.eventID.toString()}")
//            holder.itemView.context.startActivity(intent)

            val jRef = fStore.collection("users").document(userID!!).collection("upcoming events").document(eventIDNumber!!)
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


    override fun getItemCount(): Int {
        return bookmarkList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.showTopicName)
        val eventDate: TextView = itemView.findViewById(R.id.showTopicDescription)
        val eventTime: TextView = itemView.findViewById(R.id.showTime)
        val eventID: TextView = itemView.findViewById(R.id.tvEventID)
        val eventLocation: TextView = itemView.findViewById(R.id.showLocation)
        val ivBookmark: ImageView = itemView.findViewById(R.id.ivBookmark)

    }
}