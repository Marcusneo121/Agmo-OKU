package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.custom_dialog_read.*
import kotlinx.android.synthetic.main.custom_dialog_read.view.*
import kotlinx.android.synthetic.main.custom_dialog_yes_no_cancel.view.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityQuitEventBinding
import my.edu.tarc.okuappg11.databinding.CustomDialogReadBinding
import java.io.File

class QuitEventActivity : AppCompatActivity() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityQuitEventBinding
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
        binding = ActivityQuitEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid
        changeBtnColor()
        readData(eventID)


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

        binding.btnQuitEvent.setOnClickListener {
            /*MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Do you want to quit this event?")
                .setPositiveButton("Yes") { dialog, which ->*/
            val dView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_yes_no_cancel, null)
            val dBuilder = AlertDialog.Builder(this)
                .setView(dView)
                .setTitle("Do you want to quit this event?")
            val dAlertDialog = dBuilder.show()

            dView.btnDialogYes.setOnClickListener {
                val mView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_read, null)
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
                    }
                    fStore.collection("users").document(userID!!).collection("upcoming events")
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
                            val intent =
                                Intent(this@QuitEventActivity, AllUpcomingEvents::class.java)
                            startActivity(intent)

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

                mView.btnCancelReason.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }

            dView.btnDialogNo.setOnClickListener {
                dAlertDialog.dismiss()
            }

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

                } else if ( document.get("eventUID ") == null){
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
    }
}


