package my.edu.tarc.okuappg11.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAddEventBinding
import my.edu.tarc.okuappg11.databinding.ActivityAddStoryBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.progressdialog.AddEventDialog
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val dialogAddEvent = AddEventDialog(this)

    private var firestoreCheck:Boolean = false
    private var storageCheck:Boolean = false
    private var mSelectedImageFileUri: Uri? = null
    private var storyId:String? = null
    private var storyTitle:String? = null
    private var storyThumbnailDescription:String? = null
    private var storyDescription:String? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Add Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        //loadData()
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val ref: DocumentReference = fStore.collection("stories").document()
        storyId = ref.id

        binding.ivStoryThumbnail.setOnClickListener{
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ){
                //Toast.makeText(this,"You already have storage permission", Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)



            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnPost.setOnClickListener {
            if (validateStoryDetails()){
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.dialog_add_story_title))
                    .setMessage(resources.getString(R.string.dialog_add_story_description))
                    .setNegativeButton(resources.getString(R.string.dialog_add_negative)) { dialog, which ->
                        Toast.makeText(this, R.string.add_cancel, Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(resources.getString(R.string.dialog_add_story_positive)) { dialog, which ->
                        storyTitle = binding.textFieldStoryTitle.editText!!.text.toString()
                        storyThumbnailDescription =
                            binding.textFieldStoryThumbnailDescription.editText!!.text.toString()
                        storyDescription =
                            binding.textFieldStoryDescription.editText!!.text.toString()
                        val dateNow = Calendar.getInstance().time
                        val formattedDateNow = SimpleDateFormat("dd/MM/yyyy").format(dateNow)



                        Log.d("check", storyId.toString())
                        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                            Constants.STORY_IMAGE + storyId + ".jpg"

                        )
                        dialogAddEvent.startLoading()
                        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                            Log.e(
                                "Firebase Image",
                                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                            )

                            taskSnapshot.metadata!!.reference!!.downloadUrl
                                .addOnSuccessListener { uri ->
                                    Log.e("Downloadable Image URL", uri.toString())
                                    val hashMapStory = hashMapOf(
                                        "storyTitle" to storyTitle!!.capitalize(),
                                        "storyThumbnailDescription" to storyThumbnailDescription,
                                        "storyDescription" to storyDescription,
                                        "storyCreatedDate" to formattedDateNow,
                                        "storyThumbnailURL" to uri.toString()

                                    )

                                    ref.set(hashMapStory)
                                        .addOnSuccessListener {
                                            Log.d(ContentValues.TAG, "Added Document")
                                            Toast.makeText(this,R.string.add_story_success,Toast.LENGTH_SHORT).show()
                                            firestoreCheck = true

                                        }
                                        .addOnFailureListener {
                                            Log.w(
                                                ContentValues.TAG,
                                                "Error adding document ${it.suppressedExceptions}"
                                            )
                                        }
                                    storageCheck = true
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("ERROR", exception.message.toString())
                                }
                        }

                        val handler = Handler()
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                //  val sharedPreferences:SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                                //  val editor = sharedPreferences.edit()
                                // editor.clear()
                                // editor.apply()
                                val intent =
                                    Intent(this@AddStoryActivity, AdminStoryDetails::class.java)
                                intent.putExtra("StoryUID", "${storyId.toString()}")
                                intent.putExtra("accessBy","admin")
                                intent.putExtra("addedBy","admin")
                                startActivity(intent)
                                dialogAddEvent.isDismiss()

                            }
                        }, 6000)
                    }.show()

            }else{
                Toast.makeText(this, R.string.event_validation_error, Toast.LENGTH_LONG).show()

            }


        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            //granted
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this,"Storage permission granted", Toast.LENGTH_LONG).show()
                Constants.showImageChooser(this)


            }else{
                //display another toast if permission not granted
                Toast.makeText(this,R.string.storage_permission_denied, Toast.LENGTH_LONG).show()

            }
        }



    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    try {
                        //uri of image
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivStoryThumbnail
                        )
                    } catch (e: Exception) {
                        Toast.makeText(this, R.string.image_selection_fail, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("REQUEST CANCELLED", "Image selection cancelled")
        }
    }

    private fun validateStoryDetails(): Boolean {
        if (binding.textFieldStoryDescription.editText!!.text.isEmpty() ||
            binding.textFieldStoryThumbnailDescription.editText!!.text.isEmpty() ||
            binding.textFieldStoryTitle.editText!!.text.isEmpty()){
            return false
        }else{
            return true
        }
    }
}