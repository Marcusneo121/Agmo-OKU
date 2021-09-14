package my.edu.tarc.okuappg11.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import my.edu.tarc.okuappg11.databinding.ActivityAdminUpdateStoryBinding
import my.edu.tarc.okuappg11.databinding.ActivityUpdateEventBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.progressdialog.AddEventDialog
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class AdminUpdateStory : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUpdateStoryBinding
    private val dialogAddEvent = AddEventDialog(this)

    private var firestoreCheck: Boolean = false
    private var storageCheck: Boolean = false
    private var mSelectedImageFileUri: Uri? = null
    private var storyId: String? = null
    private var storyTitle: String? = null
    private var storyThumbnailDescription: String? = null
    private var storyDescription: String? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var storyThumbnailURL: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_update_story)
        binding = ActivityAdminUpdateStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        storyId = intent.getStringExtra("StoryUID")
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()



        getData()

        binding.ivUpdateStoryThumbnail.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Toast.makeText(this,"You already have storage permission", Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)


            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnUpdateStory.setOnClickListener {
            if (validateStoryDetails()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.dialog_update_title))
                    .setMessage(resources.getString(R.string.dialog_update_description))
                    .setNegativeButton(resources.getString(R.string.dialog_update_negative)) { dialog, which ->
                        Toast.makeText(this,R.string.update_cancel,Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(resources.getString(R.string.dialog_update_positive)) { dialog, which ->
                        storyTitle = binding.textFieldUpdateStoryTitle.editText!!.text.toString()
                        storyThumbnailDescription = binding.textFieldUpdateStoryThumbnailDescription.editText!!.text.toString()
                        storyDescription = binding.textFieldUpdateStoryDescription.editText!!.text.toString()
                        val dateNow = Calendar.getInstance().time
                        val formattedDateNow = SimpleDateFormat("dd/MM/yyyy").format(dateNow)


                        Log.d("check", storyId.toString())
                        if(mSelectedImageFileUri != null) {

                            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                                Constants.STORY_IMAGE + storyId + "."
                                        + Constants.getFileExtension(
                                    this,
                                    mSelectedImageFileUri
                                )
                            )


                            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                                Log.e(
                                    "Firebase Image",
                                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                                )

                                taskSnapshot.metadata!!.reference!!.downloadUrl
                                    .addOnSuccessListener { uri ->
                                        Log.e("Downloadable Image URL", uri.toString())
                                        storyThumbnailURL = uri.toString()
                                        storageCheck = true
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("ERROR", exception.message.toString())
                                    }
                            }
                        }

                        dialogAddEvent.startLoading()
                        val hashMapEvents = hashMapOf(
                            "storyTitle" to storyTitle,
                            "storyThumbnailDescription" to storyThumbnailDescription,
                            "storyDescription" to storyDescription,
                            "storyThumbnailURL" to storyThumbnailURL

                        )
                        val ref: DocumentReference = fStore.collection("stories").document(storyId!!)

                        ref.update(hashMapEvents as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this,R.string.update_success,Toast.LENGTH_SHORT).show()
                                Log.d(ContentValues.TAG, "Added Document")
                                firestoreCheck=true

                            }
                            .addOnFailureListener {
                                Log.w(ContentValues.TAG, "Error adding document ${it.suppressedExceptions}")
                            }

                        val handler = Handler()
                        handler.postDelayed(object: Runnable{
                            override fun run() {
                                //  val sharedPreferences:SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                                //  val editor = sharedPreferences.edit()
                                // editor.clear()
                                // editor.apply()
                                val intent = Intent(this@AdminUpdateStory, AdminStoryDetails::class.java)
                                intent.putExtra("EventUID","${storyId.toString()}")
                                startActivity(intent)
                                dialogAddEvent.isDismiss()

                            }
                        }, 5000)
                    }
                    .show()


            }else{
                Toast.makeText(this, R.string.event_validation_error, Toast.LENGTH_LONG).show()

            }




            }


        }


        private fun getData() {
            val docRef = fStore.collection("stories").document(storyId!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        storyTitle = document.getString("storyTitle")
                        storyThumbnailDescription = document.getString("storyThumbnailDescription")
                        storyDescription = document.getString("storyDescription")
                        storyThumbnailURL = document.getString("storyThumbnailURL")
                        binding.textFieldUpdateStoryTitle.editText!!.setText(storyTitle)
                        binding.textFieldUpdateStoryThumbnailDescription.editText!!.setText(
                            storyThumbnailDescription
                        )
                        binding.textFieldUpdateStoryDescription.editText!!.setText(storyDescription)



                        GlideLoader(this).loadUserPicture(
                            Uri.parse(storyThumbnailURL)!!,
                            binding.ivUpdateStoryThumbnail
                        )

                    } else {
                        Log.d("HEY", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
                //granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this,"Storage permission granted", Toast.LENGTH_LONG).show()
                    Constants.showImageChooser(this)


                } else {
                    //display another toast if permission not granted
                    Toast.makeText(this, R.string.storage_permission_denied, Toast.LENGTH_LONG)
                        .show()

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
                                binding.ivUpdateStoryThumbnail
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
            if (binding.textFieldUpdateStoryTitle.editText!!.text.isEmpty() ||
                binding.textFieldUpdateStoryThumbnailDescription.editText!!.text.isEmpty() ||
                binding.textFieldUpdateStoryDescription.editText!!.text.isEmpty()
            ) {
                return false
            } else {
                return true
            }
        }



}