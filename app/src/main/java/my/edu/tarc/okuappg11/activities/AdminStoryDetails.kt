package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_admin_story_details.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAdminEventDetailsBinding
import my.edu.tarc.okuappg11.databinding.ActivityAdminStoryDetailsBinding
import java.io.File

class AdminStoryDetails : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityAdminStoryDetailsBinding
    private var storyTitle:String? = null
    private var storyDescription:String? = null
    private var userID: String? = null
    private var likesCheck:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminStoryDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val storyId = intent.getStringExtra("StoryUID")
        val accessBy = intent.getStringExtra("accessBy")

        if(accessBy == "admin"){
            linearLayout5.visibility = View.VISIBLE

        }else{
            linearLayout5.visibility = View.GONE

        }

        binding.btnLike.setOnClickListener {
            fAuth = FirebaseAuth.getInstance()
            fStore = FirebaseFirestore.getInstance()
            /*val eventId = intent.getStringExtra("EventUID")*/
            userID = fAuth.currentUser!!.uid

            val docRef = fStore.collection("users").document(userID!!)
                .collection("likes")
                .document(storyId.toString())
            docRef.get()
                .addOnSuccessListener { document ->
                    Log.d("check",document.toString()
                    )
                    if ( document.get("storyUID") != null ){
                        fStore.collection("users").document(userID!!).collection("likes")
                            .document(storyId!!)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("check", "CHECKDELETE")
                                likesCheck = false
                                binding.btnLike.setBackgroundColor(Color.TRANSPARENT)

                                /*val likebtn: Button = findViewById(R.id.btnLike)
                                val drawable =
                                    resources.getDrawable(R.drawable.ic_baseline_thumb_up_24).mutate()
                                drawable.setColorFilter(
                                    resources.getColor(R.color.white),
                                    PorterDuff.Mode.SRC_ATOP
                                )

                                likebtn.setCompoundDrawables(drawable, null, null, null)*/
                            }.addOnFailureListener {


                                Log.e("error",it.message.toString())
                            }
                    }else if (document.get("storyUID") == null){
                        Log.d("check", "CHECK")

                        val hashmapLikes = hashMapOf(
                            "storyUID" to storyId,
                            "storyTitle" to storyTitle
                        )

                        fStore.collection("users").document(userID!!).collection("likes")
                            .document(storyId!!)
                            .set(hashmapLikes)
                            .addOnSuccessListener {
                                Log.d("check", "CHECKADD")

                                binding.btnLike.setBackgroundColor(Color.WHITE)
                            }.addOnFailureListener {
                                Log.e("error",it.message.toString())
                            }
                    }

                }
                .addOnFailureListener { exception ->
                    likesCheck = false
                    Log.e("error",exception.message.toString())
                }
        }

        binding.btnUpdateStory.setOnClickListener {

            val intent = Intent(this@AdminStoryDetails, AdminUpdateStory::class.java)
            intent.putExtra("StoryUID", storyId)
            startActivity(intent)

        }

        binding.btnDeleteStory.setOnClickListener {
            fStore = FirebaseFirestore.getInstance()

            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.dialog_delete_story_title))
                .setMessage(resources.getString(R.string.dialog_delete_story_description))
                .setNegativeButton(resources.getString(R.string.dialog_delete_story_negative)) { dialog, which ->
                    Toast.makeText(this, R.string.delete_cancel, Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton(resources.getString(R.string.dialog_delete_story_positive)) { dialog, which ->
                    fStore.collection("stories").document(storyId!!)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }.addOnFailureListener {
                            Log.d("error", it.message.toString())
                        }
                }.show()
        }

        readData(storyId)

    }

    private fun readData(storyId: String?) {
        val docRef = fStore.collection("stories").document(storyId.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    storyTitle =  document.getString("storyTitle")
                    storyDescription = document.getString("storyDescription")


                    supportActionBar?.title = storyTitle
                    binding.tvAdminStoryTitle.text = storyTitle
                    binding.tvAdminStoryDescription.text = storyDescription

                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }


        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("STORY_THUMBNAIL${storyId}.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        sRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.ivAdminStoryDetailsThumbnail.setImageBitmap(bitmap)
            Log.d("CHECK", " IMAGE LOADED")
        }.addOnFailureListener{
            Log.d("CHECK", it.message.toString())
            Log.d("CHECK", "STORY_THUMBNAIL${storyId}.jpg")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}