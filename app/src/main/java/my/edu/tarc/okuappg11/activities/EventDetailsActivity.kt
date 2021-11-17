package my.edu.tarc.okuappg11.activities

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.blue
import android.graphics.Insets.add
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_event_details.*
import kotlinx.android.synthetic.main.custom_dialog_yes_no_cancel.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityEventDetailsBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.progressdialog.AddEventDialog
import my.edu.tarc.okuappg11.recyclerview.CommentsAdapter
import my.edu.tarc.okuappg11.recyclerview.CommentsArrayList
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private var userRole: String? = null
    private var latitude:String? = null
    private var longitude:String? = null
    private var userDisplayName:String?=null
    private var userProfileImageUri:String?= null
    private lateinit var commentsArrayList: ArrayList<CommentsArrayList>
    private lateinit var commentsAdapter: CommentsAdapter

    private lateinit var recyclerViewComment : RecyclerView
    private var pressedHideShow: Boolean = false

    private var currentImageURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnUnbookmark.visibility = View.INVISIBLE
        binding.btnBookmark.visibility = View.INVISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("EventUID")
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid

        recyclerViewComment = binding.rvComments
        recyclerViewComment.layoutManager = LinearLayoutManager(this)
        recyclerViewComment.setHasFixedSize(true)
        commentsArrayList = arrayListOf()
        commentsAdapter = CommentsAdapter(commentsArrayList)

        recyclerViewComment.adapter = commentsAdapter
        binding.lyComments.visibility = View.GONE
        binding.btnShowHideComment.text = "Show Comments"
        pressedHideShow = true
        readComment()


        readBookmark()
        readData(eventId)

        binding.btnShowHideComment.setOnClickListener {
            if(!pressedHideShow){
                binding.lyComments.visibility = View.GONE
                binding.btnShowHideComment.text = "Show Comments"
                pressedHideShow = true

            } else {
                binding.lyComments.visibility = View.VISIBLE
                binding.btnShowHideComment.text = "Hide Comments"
                pressedHideShow = false
            }
        }

        binding.btnSubmitComment.setOnClickListener {

            if (binding.editTextComment.text.isEmpty()) {
                binding.editTextComment.error = "Please enter comment."
                return@setOnClickListener
            }

            val commentDetails = binding.editTextComment.text.toString()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val strDate: String = dateFormat.format(date).toString()

            val hashmapComment = hashMapOf(
                "userUID" to userID,
                "commentDetails" to commentDetails,
                "commentDate" to strDate
            )

            fStore.collection("events").document(eventId!!).collection("comments").document()
                .set(hashmapComment)
                .addOnSuccessListener {
                    commentsAdapter.notifyDataSetChanged()
                    Log.d("check","comments saved")
                    binding.editTextComment.setText("")
                    binding.editTextComment.isFocusable = false
                    Toast.makeText(this,"Comment successfully",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.e("error", it.message.toString())
                }
        }

        binding.btnSeeAllComment.setOnClickListener {
            commentsArrayList.clear()
            val intent= Intent(this, SeeAllComment::class.java)
            intent.putExtra("EventUID",eventID)
            startActivity(intent)

        }

        binding.btnUnbookmark.setOnClickListener {
            bookmark()
        }

        binding.btnBookmark.setOnClickListener {
            unBookmark()
        }

        binding.btnJoinEvent.setOnClickListener {
            /*val dView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_yes_no_cancel, null)
            val dBuilder = AlertDialog.Builder(this)
                .setView(dView)
                .setTitle("Do you want to join this event?")
            val dAlertDialog = dBuilder.show()
            dView.btnDialogYes.setOnClickListener {*/
                val intent = Intent(this@EventDetailsActivity, JoinEvent::class.java)
                intent.putExtra("EventUID","${eventId.toString()}")
                intent.putExtra("EventName","${eventName.toString()}")
                intent.putExtra("StartDate","${startDate.toString()}")
                intent.putExtra("StartTime","${startTime.toString()}")
                startActivity(intent)
            /*}
            dView.btnDialogNo.setOnClickListener {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                dAlertDialog.dismiss()
            }*/
        }

        binding.btnVolunteer.setOnClickListener(){
            val intent = Intent(this@EventDetailsActivity, VolunteerRegisterActivity::class.java)
            intent.putExtra("EventUID","${eventId.toString()}")
            startActivity(intent)
        }

        binding.tvEventLocation.setOnClickListener{
            val locationUri = Uri.parse("geo:${latitude},${longitude}?q=${eventLocation}")
            val locationIntent = Intent(Intent.ACTION_VIEW,locationUri)
            locationIntent.setPackage("com.google.android.apps.maps")
            locationIntent.resolveActivity(packageManager)?.let{
                startActivity(locationIntent)
            }
        }


    }



    private fun readComment() {
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventID!!)
            .collection("comments")
            .orderBy("commentDate", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    commentsArrayList.clear()
                    value?.forEach {
                        Log.d("checkid",it.getString("userUID").toString())
                        fStore.collection("users").document(it.getString("userUID").toString()).get()
                            .addOnSuccessListener { dc ->

                                userDisplayName = dc.getString("name").toString()
                                userProfileImageUri = dc.getString("profileImageURL").toString()
                                Log.d("CHECKoutside", userProfileImageUri.toString())

                                val userCommentDetails = it.toObject(CommentsArrayList::class.java)
                                if (userCommentDetails != null){
                                    userCommentDetails.displayName = userDisplayName.toString()
                                    userCommentDetails.userImageUri = userProfileImageUri.toString()
                                    userCommentDetails.userID = it.getString("userUID").toString()
                                    userCommentDetails.commentDetails = it.getString("commentDetails").toString()
                                    userCommentDetails.commentDate = it.getString("commentDate").toString()
                                    commentsArrayList.add(userCommentDetails)
                                }
                            }.addOnFailureListener {
                                Log.e("error", it.message.toString())
                            }
                        Log.d("CHECKout", userDisplayName.toString())

                        commentsAdapter.notifyDataSetChanged()

                        if (commentsArrayList.isEmpty()) {
                            Log.d("try again", "Array list is empty")
                        } else {
                            Log.d("Got array", "Array list is not empty")
                        }
                    }
                }
            })

            }

    override fun onResume() {
        super.onResume()
        readData(eventID)
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
                    latitude = document.get("latitude").toString()
                    longitude = document.get("longitude").toString()
                    currentImageURL = document.getString("eventThumbnailURL")
                    GlideLoader(this)
                        .loadUserPicture(Uri.parse(currentImageURL.toString()), binding.ivEventDetailsThumbnail)

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

        //OKU volunteer validation
        fStore.collection("users").document(userID!!).get().addOnSuccessListener { it ->
            userRole = it.get("userType").toString()
            if(userRole == "Normal" ){
                binding.btnVolunteer.visibility=View.VISIBLE
            } else {
                binding.btnVolunteer.visibility=View.GONE
            }
            }

        // volunteer validation
        val dRef = fStore.collection("users").document(userID!!).collection("volunteerEvent").document(eventId.toString())
        dRef.get()
            .addOnSuccessListener { document ->
                if (document.get("eventId") != null) {
                    binding.btnVolunteer.setText("Applied Volunteer")
                    binding.btnVolunteer.setOnClickListener() {
                        Toast.makeText(this, "You are a volunteer!", Toast.LENGTH_SHORT).show()
                    }

                    binding.btnJoinEvent.isEnabled = false
                    binding.btnJoinEvent.setText("Volunteered")
                    binding.btnJoinEvent.setBackgroundColor(Color.parseColor("#2D54C0"))


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