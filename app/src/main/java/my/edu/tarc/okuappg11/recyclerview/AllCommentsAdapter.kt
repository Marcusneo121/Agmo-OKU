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

class AllCommentsAdapter(private val allCommentsArrayListList: ArrayList<AllCommentsArrayList>):RecyclerView.Adapter<AllCommentsAdapter.ViewHolder>() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCommentsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment,parent,false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: AllCommentsAdapter.ViewHolder, position: Int) {
        val allCommentsArrayListItem:AllCommentsArrayList = allCommentsArrayListList[position]
        holder.itemUsername.text = allCommentsArrayListItem.displayName
        holder.itemUserComment.text = allCommentsArrayListItem.commentDetails
        holder.itemTime.text = allCommentsArrayListItem.commentDate


        if(allCommentsArrayListItem.userImageUri != "null"){
            GlideLoader(holder.itemImage.context).loadUserPicture(Uri.parse(allCommentsArrayListItem.userImageUri),holder.itemImage)
        }


    }

    override fun getItemCount(): Int {
        return allCommentsArrayListList.size
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