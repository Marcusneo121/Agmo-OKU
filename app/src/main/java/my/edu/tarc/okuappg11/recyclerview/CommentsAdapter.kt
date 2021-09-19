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

class CommentsAdapter(private val commentsArrayListList: ArrayList<CommentsArrayList>):RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
        val commentsArrayListItem:CommentsArrayList = commentsArrayListList[position]
        holder.itemUsername.text = commentsArrayListItem.displayName
        holder.itemUserComment.text = commentsArrayListItem.commentDetails
        holder.itemTime.text = commentsArrayListItem.commentDate


        if(commentsArrayListItem.userImageUri != "null"){
            GlideLoader(holder.itemImage.context).loadUserPicture(Uri.parse(commentsArrayListItem.userImageUri),holder.itemImage)
        }


    }

    override fun getItemCount(): Int {
        return commentsArrayListList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemUsername : TextView
        var itemUserComment : TextView
        var itemTime : TextView


        init{
            itemImage = itemView.findViewById(R.id.commentUserImg)
            itemUsername = itemView.findViewById(R.id.tvUsername)
            itemUserComment = itemView.findViewById(R.id.tvUserComment)
            itemTime = itemView.findViewById(R.id.tvPostedTime)





        }
    }
}