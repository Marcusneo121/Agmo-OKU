package my.edu.tarc.okuappg11.models

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val READ_STORAGE_PERMISSION_CODE  = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val PLACE_PICKER_REQUEST =1
    const val USER_PROFILE_IMAGE:String = "EVENT_THUMBNAIL"
    const val USER_IMAGE:String = "PROFILE_IMAGE"
    const val STORY_IMAGE:String = "STORY_THUMBNAIL"


    fun showImageChooser(activity: Activity){
        val galleryIntent= Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        //launches image selection of phone storage using the constant code
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)


    }

    fun getFileExtension(activity:Activity, uri: Uri?):String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}