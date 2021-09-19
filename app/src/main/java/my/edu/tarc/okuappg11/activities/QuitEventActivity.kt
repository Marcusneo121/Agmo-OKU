package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.custom_dialog_read.*
import kotlinx.android.synthetic.main.custom_dialog_read.view.*
import kotlinx.android.synthetic.main.custom_dialog_yes_no_cancel.view.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityQuitEventBinding
import my.edu.tarc.okuappg11.databinding.CustomDialogReadBinding
import my.edu.tarc.okuappg11.recyclerview.CommentsAdapter
import my.edu.tarc.okuappg11.recyclerview.CommentsArrayList
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QuitEventActivity : AppCompatActivity() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityQuitEventBinding
    private var eventName: String? = null
    private var eventDescription: String? = null
    private var eventLocation: String? = null
    private var startDate: String? = null
    private var startTime: String? = null
    private lateinit var bmArrayList: ArrayList<BookmarkArrayList>
    private lateinit var bmAdapter: BookmarkAdapter
    private var userID: String? = null
    private var eventID: String? = null
    private var bookmarkCheck: Boolean = false
    private var latitude: String? = null
    private var longitude: String? = null
    private var userDisplayName: String? = null
    private var userProfileImageUri: String? = null
    private lateinit var commentsArrayList: ArrayList<CommentsArrayList>
    private lateinit var commentsAdapter: CommentsAdapter

    private lateinit var recyclerViewComment: RecyclerView
    private var pressedHideShow: Boolean = false

    private var currentImageURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuitEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnUnbookmark.visibility = View.INVISIBLE
        binding.btnBookmark.visibility = View.INVISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid
        // changeBtnColor()

        recyclerViewComment = binding.rvCommentsQuitEv
        recyclerViewComment.layoutManager = LinearLayoutManager(this)
        recyclerViewComment.setHasFixedSize(true)
        commentsArrayList = arrayListOf()
        commentsAdapter = CommentsAdapter(commentsArrayList)

        recyclerViewComment.adapter = commentsAdapter
        binding.lyCommentsQuitEv.visibility = View.GONE
        binding.btnShowHideCommentQuitEv.text = "Show Comments"
        pressedHideShow = true
        readComment()


        readBookmark()

        binding.btnShowHideCommentQuitEv.setOnClickListener {
            if (!pressedHideShow) {
                binding.lyCommentsQuitEv.visibility = View.GONE
                binding.btnShowHideCommentQuitEv.text = "Show Comments"
                pressedHideShow = true

            } else {
                binding.lyCommentsQuitEv.visibility = View.VISIBLE
                binding.btnShowHideCommentQuitEv.text = "Hide Comments"
                pressedHideShow = false
            }
        }

        binding.btnSubmitCommentQuitEv.setOnClickListener {
            val commentDetails = binding.editTextCommentQuitEv.text.toString()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val strDate: String = dateFormat.format(date).toString()

            val hashmapComment = hashMapOf(
                "userUID" to userID,
                "commentDetails" to commentDetails,
                "commentDate" to strDate
            )

            fStore.collection("events").document(eventID!!).collection("comments").document()
                .set(hashmapComment)
                .addOnSuccessListener {
                    commentsAdapter.notifyDataSetChanged()
                    Log.d("check", "comments saved")
                    binding.editTextCommentQuitEv.setText("")
                    binding.editTextCommentQuitEv.isFocusable = false
                    Toast.makeText(this, "Comment successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.e("error", it.message.toString())
                }
        }

        binding.btnSeeAllCommentQuitEv.setOnClickListener {
            commentsArrayList.clear()
            val intent = Intent(this, SeeAllComment::class.java)
            intent.putExtra("EventUID", eventID)
            startActivity(intent)

        }


        binding.tvEventLocation.setOnClickListener {
            val locationUri = Uri.parse("geo:${latitude},${longitude}?q=${eventLocation}")
            val locationIntent = Intent(Intent.ACTION_VIEW, locationUri)
            locationIntent.setPackage("com.google.android.apps.maps")
            locationIntent.resolveActivity(packageManager)?.let {
                startActivity(locationIntent)
            }
        }

        binding.btnUnbookmark.setOnClickListener {
            bookmark()
        }

        binding.btnBookmark.setOnClickListener {
            unBookmark()
        }

        binding.btnQuitEvent.setOnClickListener {
            /*MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Do you want to quit this event?")
                .setPositiveButton("Yes") { dialog, which ->*/
            val dView =
                LayoutInflater.from(this).inflate(R.layout.custom_dialog_yes_no_cancel, null)
            val dBuilder = AlertDialog.Builder(this)
                .setView(dView)
                .setTitle("Do you want to quit this event?")
            val dAlertDialog = dBuilder.show()

            dView.btnDialogYes.setOnClickListener {
                /*val mView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_read, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(mView)
                    .setTitle("Reason for Quitting")
                val mAlertDialog = mBuilder.show()

                mView.btnSubmitReason.setOnClickListener {
                    val quitReason = mView.etReason.text.toString()
                    Log.d("check", "submit reason")
                    if (mView.etReason.text.isEmpty()) {
                        mView.etReason.error = "Please enter your reason."
                        return@setOnClickListener
                    }*/
                fStore.collection("users").document(userID!!).collection("upcomingEvents")
                    .document(eventID!!)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("check", "CHECKDELETE")
                        Toast.makeText(
                            this,
                            "You have quit this event.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
//                            val intent =
//                                Intent(this@QuitEventActivity, AllUpcomingEvents::class.java)
//                            startActivity(intent)

                    }.addOnFailureListener {
                        Log.e("error", it.message.toString())
                    }
                //quit as participants
                fStore.collection("events").document(eventID!!).collection("participants")
                    .document(userID!!)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("check", "CHECKDELETE")

                    }.addOnFailureListener {
                        Log.e("error", it.message.toString())
                    }
            }
            dView.btnDialogNo.setOnClickListener {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                dAlertDialog.dismiss()
            }

            /*mView.btnCancelReason.setOnClickListener {
                mAlertDialog.dismiss()
            }*/
        }

        readData(eventID)
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
                        Log.d("checkid", it.getString("userUID").toString())
                        fStore.collection("users").document(it.getString("userUID").toString())
                            .get()
                            .addOnSuccessListener { dc ->

                                userDisplayName = dc.getString("name").toString()
                                userProfileImageUri = dc.getString("profileImageURL").toString()
                                Log.d("CHECKoutside", userProfileImageUri.toString())

                                val userCommentDetails = it.toObject(CommentsArrayList::class.java)
                                if (userCommentDetails != null) {
                                    userCommentDetails.displayName = userDisplayName.toString()
                                    userCommentDetails.userImageUri = userProfileImageUri.toString()
                                    userCommentDetails.userID = it.getString("userUID").toString()
                                    userCommentDetails.commentDetails =
                                        it.getString("commentDetails").toString()
                                    userCommentDetails.commentDate =
                                        it.getString("commentDate").toString()
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
                Log.d(
                    "check", document.toString()
                )
                if (document.get("eventUID") != null) {
                    fStore.collection("users").document(userID!!).collection("bookmarks")
                        .document(eventID!!)
                        .delete()
                        .addOnSuccessListener {
                            binding.btnBookmark.visibility = View.INVISIBLE
                            binding.btnUnbookmark.visibility = View.VISIBLE
                            Log.d("check", "CHECKDELETE")
                            bookmarkCheck = false
                        }.addOnFailureListener {
                            Log.e("error", it.message.toString())
                        }
                }

            }
            .addOnFailureListener { exception ->
                bookmarkCheck = false
                Log.e("error", exception.message.toString())
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
                Log.d(
                    "check", document.toString()
                )
                if (document.get("eventUID") == null) {

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
                            Log.e("error", it.message.toString())
                        }
                }

            }
            .addOnFailureListener { exception ->
                bookmarkCheck = false
                Log.e("error", exception.message.toString())
            }
        //btnBookmark.setBackgroundColor(ContextCompat.getColor(this,R.blue))
    }

    private fun readBookmark() {
        val docRef = fStore.collection("users").document(userID!!)
            .collection("bookmarks")
            .document(eventID.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                Log.d("check", document.toString())
                if (document.get("eventUID") == eventID) {
                    binding.btnBookmark.visibility = View.VISIBLE
                    Log.d("check", "hey")

                } else if (document.get("eventUID") != eventID) {
                    Log.d("check", "oi")
                    binding.btnBookmark.visibility = View.INVISIBLE
                    binding.btnUnbookmark.visibility = View.VISIBLE
                }


            }

    }

    override fun onResume() {
        super.onResume()
        readData(eventID)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
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
                    currentImageURL = document.getString("eventThumbnailURL")

                    GlideLoader(this)
                        .loadUserPicture(
                            Uri.parse(currentImageURL.toString()),
                            binding.ivEventDetailsThumbnail
                        )

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
    }
}


