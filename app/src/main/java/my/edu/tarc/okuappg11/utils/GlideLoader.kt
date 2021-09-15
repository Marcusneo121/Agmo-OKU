package my.edu.tarc.okuappg11.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.recyclerview.EventsAdapter

class GlideLoader(val context: Context) {
    fun loadUserPicture(imageURI: Uri, imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.imageplaceholder)
                .into(imageView)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }
}