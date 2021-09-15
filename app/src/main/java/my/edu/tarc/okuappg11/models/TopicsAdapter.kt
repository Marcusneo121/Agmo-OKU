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
import my.edu.tarc.okuappg11.activities.AdminStoryDetails
import my.edu.tarc.okuappg11.utils.GlideLoader

class TopicsAdapter(var topicList: ArrayList<TopicsModel>): RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicsAdapter.TopicsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_topics, parent, false)
        return TopicsAdapter.TopicsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicsAdapter.TopicsViewHolder, position: Int) {
        val topicListItem: TopicsModel = topicList[position]
        holder.topicName.text = topicListItem.storyTitle
        holder.topicId.text = topicListItem.storyID
        holder.topicThumbnailDesc.text = topicListItem.storyThumbnailDescription
        GlideLoader(holder.topicImg.context).loadUserPicture(Uri.parse(topicListItem.storyThumbnailURL),holder.topicImg)

        val eventIDNumber: String = topicListItem.storyID

        holder.itemView.setOnClickListener(){
            //Toast.makeText(holder.itemView.context,"You clicked on item # ${position + 1}", Toast.LENGTH_SHORT).show()
            val intent = Intent(holder.itemView.context, AdminStoryDetails::class.java)
            if(topicListItem.accessBy == "admin"){
                intent.putExtra("StoryUID", eventIDNumber)
                intent.putExtra("accessBy", topicListItem.accessBy)
                holder.itemView.context.startActivity(intent)
            }else{
                intent.putExtra("StoryUID", eventIDNumber)
                holder.itemView.context.startActivity(intent)
            }

        }
    }

    class TopicsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val topicName : TextView = itemView.findViewById(R.id.nameTopic)
        val topicId : TextView = itemView.findViewById(R.id.tvIDStory)
        val topicThumbnailDesc: TextView = itemView.findViewById(R.id.storyThumbnailDesc)
        val topicImg: ImageView = itemView.findViewById(R.id.ivTopic)
    }

    override fun getItemCount(): Int {
        return topicList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}