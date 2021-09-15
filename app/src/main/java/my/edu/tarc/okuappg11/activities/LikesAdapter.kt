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


class LikesAdapter(var likesList: ArrayList<LikesArrayList>):
    RecyclerView.Adapter<LikesAdapter.ViewHolder>() {
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore
    private var eventId:String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LikesAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_likes, parent, false)
        return LikesAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LikesAdapter.ViewHolder, position: Int) {
        fStore = FirebaseFirestore.getInstance()

        val likesArrayListItem: LikesArrayList = likesList[position]
        holder.topicName.text = likesArrayListItem.storyTitle.toString()
        holder.topicThumbnailDesc.text = likesArrayListItem.storyThumbnailDescription
        GlideLoader(holder.topicImg.context).loadUserPicture(Uri.parse(likesArrayListItem.storyThumbnailURL),holder.topicImg)
        val eventIDNumber: String = likesArrayListItem.storyID

        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView.context, EventDetailsActivity::class.java)
            intent.putExtra("EventUID", eventIDNumber)
            holder.itemView.context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return likesList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicName: TextView = itemView.findViewById(R.id.showTopicName)
        val topicThumbnailDesc: TextView = itemView.findViewById(R.id.showTopicDescription)
        val topicImg: ImageView = itemView.findViewById(R.id.ivLikes)

    }
}