package my.edu.tarc.okuappg11.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.databinding.ActivityJoinEventBinding
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerRegisterBinding

class JoinEvent : AppCompatActivity() {
    private lateinit var binding: ActivityJoinEventBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var pName: EditText
    private lateinit var pEmail: EditText
    private lateinit var pPhone : EditText
    private var participantUID:String? =null
    private var participantName:String? = null
    private var userID: String? = null
    private var eventID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinEventBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Event Registration"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        val eventId = intent.getStringExtra("EventUID")
        val eventName = intent.getStringExtra("EventName")
        val startDate = intent.getStringExtra("StartDate")
        val startTime = intent.getStringExtra("StartTime")
        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid

        pName = binding.etPName
        pEmail = binding.etPEmail
        pPhone = binding.etPPhone

        getDisplayName()

        binding.btnPReset.setOnClickListener(){
            binding.etPName.setText("")
            binding.etPEmail.setText("")
            binding.etPPhone.setText("")
            binding.cBoxJoin1.isChecked = false
            binding.cBoxJoin2.isChecked = false

        }

        binding.btnPSubmit.setOnClickListener(){

            if (pName.text.isEmpty()) {
                pName.error = "Please enter full name."
                return@setOnClickListener
            }
            if (pEmail.text.isEmpty()) {
                pEmail.error = "Please enter your email address."
                return@setOnClickListener
            }
            if (pPhone.text.isEmpty()) {
                pPhone.error = "Please enter your phone number."
                return@setOnClickListener
            }

            if (binding.cBoxJoin1.isChecked == false){
                Toast.makeText(this, "Please agree with the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.cBoxJoin2.isChecked == false){
                Toast.makeText(this, "Please agree with the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerEvent(eventId, eventName, startDate, startTime)

            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun registerEvent(eventId: String?, eventName: String?, startDate: String?, startTime: String?){
        val hashmapUpcomingEvents = hashMapOf(
            "eventUID" to eventId.toString(),
            "eventName" to eventName.toString(),
            "startDate" to startDate.toString(),
            "startTime" to startTime.toString()
        )

        fStore.collection("users").document(userID!!).collection("upcomingEvents")
            .document(eventId!!)
            .set(hashmapUpcomingEvents)
            .addOnSuccessListener {
                Log.d("check", "CHECKADD")
                //val intent = Intent(this@EventDetailsActivity, AllUpcomingEvents::class.java)
                //startActivity(intent)
            }.addOnFailureListener {
                Log.e("error", it.message.toString())
            }

        val hashmapParticipants = hashMapOf(
            "userUID" to userID,
            "participantNname" to pName.text.toString(),
            "participantEmail" to pEmail.text.toString(),
            "participantPhone" to pPhone.text.toString()
        )

        fStore.collection("events").document(eventId!!).collection("participants")
            .document(userID!!)
            .set(hashmapParticipants)
            .addOnSuccessListener {
                Log.d("check", "CHECKADD")

                val intent =
                    Intent(this@JoinEvent, AllUpcomingEvents::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.e("error", it.message.toString())
            }

    }

    private fun getDisplayName() {
        val docRef = fStore.collection("users").document(fAuth.currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    participantUID=document.id
                    participantName = document.getString("name")
                    binding.etPName.setText(participantName)

                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }
}