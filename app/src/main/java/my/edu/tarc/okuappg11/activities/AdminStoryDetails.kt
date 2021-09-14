package my.edu.tarc.okuappg11.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminStoryDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val storyId = intent.getStringExtra("StoryUID")
        linearLayout5.visibility = View.GONE

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
}