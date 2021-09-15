package my.edu.tarc.okuappg11.activities

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAdminEventDetailsBinding
import my.edu.tarc.okuappg11.fragments.HomeFragment
import my.edu.tarc.okuappg11.models.Constants
import java.io.File

class AdminEventDetailsActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var binding: ActivityAdminEventDetailsBinding
    private var eventName:String? = null
    private var eventDescription:String? = null
    private var startDate:String? = null
    private var startTime:String? = null
    private var eventLocation:String? = null
    private var addedBy:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEventDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val eventId = intent.getStringExtra("EventUID")
        val eventType = intent.getStringExtra("EventType")
        addedBy = intent.getStringExtra("addedBy")

        readData(eventId)

        if (eventType == "pending"){
            binding.btnLeft.setText(R.string.pending_reject)
            binding.btnRight.setText(R.string.pending_accept)

            binding.btnLeft.setOnClickListener{
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

            binding.btnRight.setOnClickListener{
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



        }else {
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
                                finish()
                            }.addOnFailureListener {
                                Log.d("error", it.message.toString())
                            }

                    }
                    .show()

            }

            binding.btnRight.setOnClickListener {
                val intent = Intent(this@AdminEventDetailsActivity, UpdateEvent::class.java)
                intent.putExtra("EventUID", "${eventId}")
                startActivity(intent)
            }
        }

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


        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("EVENT_THUMBNAIL${eventId}.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        sRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.ivAdminEventDetailsThumbnail.setImageBitmap(bitmap)
            Log.d("CHECK", " IMAGE LOADED")
        }.addOnFailureListener{
            Log.d("CHECK", it.message.toString())
            Log.d("CHECK", "EVENT_THUMBNAIL${eventId}.jpg")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(addedBy == "admin"){
            val intent=Intent(this,AdminHomeActivity::class.java)
            startActivity(intent)
        }else if(addedBy == "eventorganizer"){
            val intent= Intent(this,HomeFragment::class.java)
            startActivity(intent)
        }
    }

}