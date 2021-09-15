package my.edu.tarc.okuappg11.activities

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.blue
import android.graphics.Insets.add
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_event_details.*
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
    private var bookmarkCheck:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("EventUID")
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid
        changeBtnColor()
        readData(eventId)

        binding.btnBookmark.setOnClickListener {

            fAuth = FirebaseAuth.getInstance()
            fStore = FirebaseFirestore.getInstance()
            /*val eventId = intent.getStringExtra("EventUID")*/
            userID = fAuth.currentUser!!.uid

            val docRef = fStore.collection("users").document(userID!!)
                .collection("bookmarks")
                .document(eventID.toString())
            docRef.get()
                .addOnSuccessListener { document ->
                    Log.d("check",document.toString()
                    )
                    if ( document.get("eventUID") != null ){
                        fStore.collection("users").document(userID!!).collection("bookmarks")
                            .document(eventID!!)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("check", "CHECKDELETE")
                                bookmarkCheck = false
                                binding.btnBookmark.setBackgroundColor(Color.TRANSPARENT)
                            }.addOnFailureListener {


                                Log.e("error",it.message.toString())
                            }
                    }else if (document.get("eventUID") == null){
                        Log.d("check", "CHECK")

                        val hashmapBookmark = hashMapOf(
                            "eventUID" to eventID,
                            "eventName" to eventName
                        )

                        fStore.collection("users").document(userID!!).collection("bookmarks")
                            .document(eventID!!)
                            .set(hashmapBookmark)
                            .addOnSuccessListener {
                                Log.d("check", "CHECKADD")

                                binding.btnBookmark.setBackgroundColor(Color.BLUE)
                            }.addOnFailureListener {
                                Log.e("error",it.message.toString())
                            }
                    }

                }
                .addOnFailureListener { exception ->
                    bookmarkCheck = false
                    Log.e("error",exception.message.toString())
                }
            //btnBookmark.setBackgroundColor(ContextCompat.getColor(this,R.blue))

        }

        binding.btnJoinEvent.setOnClickListener {
            val hashmapUpcomingEvents = hashMapOf(
                "eventUID" to eventID,
                "eventName" to eventName,
                "startDate" to startDate,
                "startTime" to startTime
            )

            fStore.collection("users").document(userID!!).collection("upcoming events")
                .document(eventID.toString())
                .set(hashmapUpcomingEvents)
                .addOnSuccessListener {
                    Log.d("check", "CHECKADD")
                    val intent = Intent(this@EventDetailsActivity, AllUpcomingEvents::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Log.e("error",it.message.toString())
                }

        }

        binding.btnVolunteer.setOnClickListener(){
            val intent = Intent(this@EventDetailsActivity, VolunteerRegisterActivity::class.java)
            intent.putExtra("EventUID","${eventId.toString()}")
            startActivity(intent)
        }
    }

    private fun changeBtnColor(){
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        /*val eventId = intent.getStringExtra("EventUID")*/
        userID = fAuth.currentUser!!.uid

        val docRef = fStore.collection("users").document(userID!!)
            .collection("bookmarks")
            .document(eventID.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.get("eventUID") != null) {
                    Log.d("check", document.toString())
                    binding.btnBookmark.setBackgroundColor(Color.BLUE)

                } else if ( document.get("eventUID") == null){
                    binding.btnBookmark.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
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
        val dRef = fStore.collection("users").document(userID!!).collection("volunteerEvent").document(eventId.toString())
        dRef.get()
            .addOnSuccessListener { document ->
                if (document.get("eventId") != null) {
                    // binding.btnVolunteer.visibility=View.INVISIBLE
                    binding.btnVolunteer.setOnClickListener() {
                        Toast.makeText(this, "You are a volunteer!", Toast.LENGTH_SHORT).show()

                    }
                } else if(document.get("eventId") == null) {
                    //binding.btnVolunteer.visibility=View.VISIBLE
                    binding.btnVolunteer.setOnClickListener() {
                        val intent =
                            Intent(this@EventDetailsActivity, VolunteerRegisterActivity::class.java)
                        intent.putExtra("EventUID", "${eventId.toString()}")
                        startActivity(intent)
                    }
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }


}