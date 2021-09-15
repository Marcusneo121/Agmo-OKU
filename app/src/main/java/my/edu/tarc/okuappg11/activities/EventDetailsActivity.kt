package my.edu.tarc.okuappg11.activities

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Insets.add
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.databinding.ActivityEventDetailsBinding
import my.edu.tarc.okuappg11.models.Constants
import java.io.File

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityEventDetailsBinding
    private var eventName:String? = null
    private var eventDescription:String? = null
    private var eventLocation:String? = null
    private var startDate:String? = null
    private var startTime:String? = null
    private lateinit var bmArrayList: ArrayList<BookmarkArrayList>
    private lateinit var bmAdapter: BookmarkAdapter
    private var userID: String? = null
    private var eventID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("EventUID")
        userID = fAuth.currentUser!!.uid

        readData(eventId)

        binding.btnBookmark.setOnClickListener {

            val hashmapBookmark = hashMapOf(
                "eventUID" to eventId,
                "eventName" to eventName
            )

            fStore.collection("users").document(userID!!).collection("bookmarks")
                .document(eventId!!)
                .set(hashmapBookmark)
                .addOnSuccessListener {
                    val intent = Intent(this@EventDetailsActivity, Bookmark::class.java)
                    startActivity(intent)
                }.addOnFailureListener {

                }

        }

        binding.btnVolunteer.setOnClickListener(){
            val intent = Intent(this@EventDetailsActivity, VolunteerRegisterActivity::class.java)
            intent.putExtra("EventUID","${eventId.toString()}")
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun readData(eventId: String?) {
        val docRef = fStore.collection("events").document(eventId.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    eventName =  document.getString("eventName")
                    eventDescription = document.getString("eventDescription")
                    startDate = document.getString("startDate")
                    startTime = document.getString("startTime")
                    eventLocation = document.getString("eventLocation")

                    supportActionBar?.title = eventName
                    binding.tvEventDate.text = startDate
                    binding.tvEventTime.text = startTime
                    binding.tvEventDescription.text = eventDescription
                    binding.tvEventLocation.text = eventLocation
                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }


        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("EVENT_THUMBNAIL${eventId}.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        sRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.ivEventDetailsThumbnail.setImageBitmap(bitmap)
            Log.d("CHECK", " IMAGE LOADED")
        }.addOnFailureListener{
            Log.d("CHECK", it.message.toString())
            Log.d("CHECK", "EVENT_THUMBNAIL${eventId}.jpg")


        }
    }


}