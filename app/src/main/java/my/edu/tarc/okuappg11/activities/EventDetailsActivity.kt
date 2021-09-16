package my.edu.tarc.okuappg11.activities

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.blue
import android.graphics.Insets.add
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import android.R.drawable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnUnbookmark.visibility = View.INVISIBLE
        binding.btnBookmark.visibility = View.INVISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("EventUID")
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid

        readBookmark()


        binding.btnUnbookmark.setOnClickListener {
            bookmark()
        }

        binding.btnBookmark.setOnClickListener {
            unBookmark()
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
                    //val intent = Intent(this@EventDetailsActivity, AllUpcomingEvents::class.java)
                    //startActivity(intent)
                }.addOnFailureListener {
                    Log.e("error",it.message.toString())
                }

            val hashmapParticipants = hashMapOf(
                "userUID" to userID,
            )

            fStore.collection("events").document(eventID!!).collection("participants")
                .document(userID!!)
                .set(hashmapParticipants)
                .addOnSuccessListener {
                    Log.d("check", "CHECKADD")
                    finish()
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

        readData(eventId)
    }

    private fun unBookmark() {
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
                    Log.d("check","ulala")
                    fStore.collection("users").document(userID!!).collection("bookmarks")
                        .document(eventID!!)
                        .delete()
                        .addOnSuccessListener {
                            binding.btnBookmark.visibility = View.INVISIBLE
                            binding.btnUnbookmark.visibility = View.VISIBLE
                            Log.d("check", "CHECKDELETE")
                            bookmarkCheck = false
                        }.addOnFailureListener {
                            Log.e("error",it.message.toString())
                        }
                }

            }
            .addOnFailureListener { exception ->
                bookmarkCheck = false
                Log.e("error",exception.message.toString())
            }
    }

    private fun bookmark() {
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
                if ( document.get("eventUID") == null ){

                    val hashmapBookmark = hashMapOf(
                        "eventUID" to eventID,
                        "eventName" to eventName
                    )

                    fStore.collection("users").document(userID!!).collection("bookmarks")
                        .document(eventID!!)
                        .set(hashmapBookmark)
                        .addOnSuccessListener {
                            binding.btnBookmark.visibility = View.VISIBLE
                            binding.btnUnbookmark.visibility = View.INVISIBLE
                            Log.d("check", "CHECKADD")

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

    private fun readBookmark() {
        val docRef = fStore.collection("users").document(userID!!)
            .collection("bookmarks")
            .document(eventID.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                Log.d("check",document.toString())
                if ( document.get("eventUID") == eventID ){
                    binding.btnBookmark.visibility = View.VISIBLE
                    Log.d("check","hey")

                }else if (document.get("eventUID") != eventID ) {
                    Log.d("check","oi")
                    binding.btnBookmark.visibility = View.INVISIBLE
                    binding.btnUnbookmark.visibility = View.VISIBLE
                }


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