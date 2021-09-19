package my.edu.tarc.okuappg11.activities

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAdminEventDetailsBinding
import my.edu.tarc.okuappg11.fragments.HomeFragment
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.recyclerview.CommentsAdapter
import my.edu.tarc.okuappg11.recyclerview.CommentsArrayList
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminEventDetailsActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityAdminEventDetailsBinding
    private var eventName: String? = null
    private var eventDescription: String? = null
    private var startDate: String? = null
    private var startTime: String? = null
    private var eventLocation: String? = null
    private var addedBy: String? = null
    private var accessBy: String? = null
    private var updatedBy:String? = null
    private var eventId: String? = null
    private var latitude:String? = null
    private var longitude:String? = null
    private lateinit var commentsArrayList: ArrayList<CommentsArrayList>
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var recyclerViewComment : RecyclerView
    private var userDisplayName:String?=null
    private var userProfileImageUri:String?= null
    private var pressedHideShow: Boolean = false
    private var userID:String? = null

    private var currentImageURI: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEventDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()


        eventId = intent.getStringExtra("EventUID")
        val eventType = intent.getStringExtra("EventType")
        addedBy = intent.getStringExtra("addedBy")
        accessBy = intent.getStringExtra("accessBy")
        updatedBy = intent.getStringExtra("updatedBy")


        recyclerViewComment = binding.rvCommentsAdminEv
        recyclerViewComment.layoutManager = LinearLayoutManager(this)
        recyclerViewComment.setHasFixedSize(true)
        commentsArrayList = arrayListOf()
        commentsAdapter = CommentsAdapter(commentsArrayList)

        recyclerViewComment.adapter = commentsAdapter
        binding.lyCommentsAdminEv.visibility = View.GONE
        binding.btnShowHideCommentAdminEv.text = "Show Comments"
        pressedHideShow = true
        readComment()


        readData(eventId)

        binding.btnShowHideCommentAdminEv.setOnClickListener {
            if(!pressedHideShow){
                binding.lyCommentsAdminEv.visibility = View.GONE
                binding.btnShowHideCommentAdminEv.text = "Show Comments"
                pressedHideShow = true

            } else {
                binding.lyCommentsAdminEv.visibility = View.VISIBLE
                binding.btnShowHideCommentAdminEv.text = "Hide Comments"
                pressedHideShow = false
            }
        }

        binding.btnSubmitCommentAdminEv.setOnClickListener {
            val commentDetails = binding.editTextCommentAdminEv.text.toString()
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
                    binding.editTextCommentAdminEv.setText("")
                    binding.editTextCommentAdminEv.isFocusable = false
                    Toast.makeText(this,"Comment successfully",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.e("error", it.message.toString())
                }
        }

        binding.btnSeeAllCommentAdminEv.setOnClickListener {
            commentsArrayList.clear()
            val intent= Intent(this, SeeAllComment::class.java)
            intent.putExtra("EventUID",eventId)
            startActivity(intent)

        }

        binding.tvAdminEventLocation.setOnClickListener {
            val locationUri = Uri.parse("geo:${latitude},${longitude}?q=${eventLocation}")
            val locationIntent = Intent(Intent.ACTION_VIEW,locationUri)
            locationIntent.setPackage("com.google.android.apps.maps")
            locationIntent.resolveActivity(packageManager)?.let{
                startActivity(locationIntent)
            }
        }

        if (eventType == "pending") {
            binding.btnLeft.setText(R.string.pending_reject)
            binding.btnRight.setText(R.string.pending_accept)

            binding.btnLeft.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.dialog_reject_title))
                    .setMessage(resources.getString(R.string.dialog_reject_description))
                    .setNegativeButton(resources.getString(R.string.dialog_reject_negative)) { dialog, which ->
                    }
                    .setPositiveButton(resources.getString(R.string.dialog_reject_positive)) { dialog, which ->
                        val ref = fStore.collection("events").document(eventId!!)

                        ref.update("status", "rejected")
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    R.string.pending_reject_message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Log.e("error", it.message.toString())
                            }
                    }.show()
            }

            binding.btnRight.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.dialog_accept_title))
                    .setMessage(resources.getString(R.string.dialog_accept_description))
                    .setNegativeButton(resources.getString(R.string.dialog_accept_negative)) { dialog, which ->
                    }
                    .setPositiveButton(resources.getString(R.string.dialog_accept_positive)) { dialog, which ->
                        val ref = fStore.collection("events").document(eventId!!)

                        ref.update("status", "accepted")
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    R.string.pending_accept_message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Log.e("error", it.message.toString())
                            }
                    }.show()
            }


        } else {
            binding.btnLeft.setText(R.string.delete_event)
            binding.btnRight.setText(R.string.update_event)
            binding.btnLeft.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.dialog_delete_title))
                    .setMessage(resources.getString(R.string.dialog_delete_description))
                    .setNegativeButton(resources.getString(R.string.dialog_delete_negative)) { dialog, which ->
                        Toast.makeText(this, R.string.delete_cancel, Toast.LENGTH_SHORT).show()
                    }
                    .setPositiveButton(resources.getString(R.string.dialog_delete_positive)) { dialog, which ->
                        fAuth = FirebaseAuth.getInstance()
                        fStore = FirebaseFirestore.getInstance()

                        fStore.collection("events").document(eventId!!)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT)
                                    .show()
                                if (accessBy == "eventorganizer") {
                                    finish()
                                    val intent = Intent(this, MyPostedEventActivity::class.java)
                                    startActivity(intent)
                                }
                                finish()
                            }.addOnFailureListener {
                                Log.d("error", it.message.toString())
                            }

                    }
                    .show()

            }

            binding.btnRight.setOnClickListener {
                finish()
                val intent = Intent(this@AdminEventDetailsActivity, UpdateEvent::class.java)
                intent.putExtra("EventUID", "${eventId}")
                startActivity(intent)
            }
        }

    }



    private fun readComment() {
        fStore = FirebaseFirestore.getInstance()
        fStore.collection("events")
            .document(eventId!!)
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

    private fun endActivity() {
        this.finish()
    }

    private fun readData(eventId: String?) {

        val docRef = fStore.collection("events").document(eventId.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    eventName = document.getString("eventName")
                    eventDescription = document.getString("eventDescription")
                    startDate = document.getString("startDate")
                    startTime = document.getString("startTime")
                    eventLocation = document.getString("eventLocation")
                    latitude = document.get("latitude").toString()
                    longitude = document.get("longitude").toString()
                    currentImageURI = document.get("eventThumbnailURL").toString()

                    GlideLoader(this)
                        .loadUserPicture(Uri.parse(currentImageURI.toString()), binding.ivAdminEventDetailsThumbnail)

                    supportActionBar?.title = eventName
                    binding.tvAdminEventDate.text = startDate
                    binding.tvAdminEventTime.text = startTime
                    binding.tvAdminEventDescription.text = eventDescription
                    binding.tvAdminEventLocation.text = eventLocation
                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onResume() {
        super.onResume()
        readData(eventId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (addedBy == "admin") {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
        } else if (addedBy == "eventorganizer") {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            //finish()
        } else if (accessBy == "eventorganizer") {
            finish()
            val intent= Intent(this,ViewEventOrganizeDetailsActivity::class.java)
            intent.putExtra("EventUID", eventId)
            startActivity(intent)
        } else if (updatedBy == "Admin"){
            val intent= Intent(this,AdminHomeActivity::class.java)
            intent.putExtra("EventUID", eventId)
            startActivity(intent)

        } else if (updatedBy == "Normal"){
            val intent= Intent(this,MyPostedEventActivity::class.java)
            startActivity(intent)

        }
    }
}