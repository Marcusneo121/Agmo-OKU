package my.edu.tarc.okuappg11.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerRegisterBinding
import java.util.regex.Pattern

class VolunteerRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerRegisterBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var vName: EditText
    private lateinit var vEmail: EditText
    private lateinit var vPhone : EditText
    private var volunteerUID:String? =null
    private var volunteerName:String? = null
    private val EMAIL_PATTERN_SCHOOL = Pattern.compile(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+\\.+[a-z]+"
    )
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )


   // private var volId:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Volunteer Registration"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))


        val eventId = intent.getStringExtra("EventUID")

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        vName = binding.etVName
        vEmail = binding.etVEmail
        vPhone = binding.etVPhone

        getDisplayName()


        binding.btnVSubmit.setOnClickListener(){

            if (vName.text.isEmpty()) {
                vName.error = "Please enter full name."
                return@setOnClickListener
            }
            if (vEmail.text.isEmpty()) {
                vEmail.error = "Please enter your email address."
                return@setOnClickListener
            }
            if (vPhone.text.isEmpty()) {
                vPhone.error = "Please enter your phone number."
                return@setOnClickListener
            }

            if (binding.vcheckBox.isChecked == false){
                Toast.makeText(this, "Please agree with the terms and conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            if(EMAIL_PATTERN.matcher(vEmail.text).matches() == false && EMAIL_PATTERN_SCHOOL.matcher(vEmail.text).matches() == false){
                Toast.makeText(this, "Please check again your email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendVolunteerRequest(eventId)
            Toast.makeText(this, "Your request has been recorded!", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnVReset.setOnClickListener(){
            //binding.etVName.setText("")
            binding.etVEmail.setText("")
            binding.etVPhone.setText("")
            binding.vcheckBox.isChecked = false

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun sendVolunteerRequest(eventId: String?){
        val hashMapVol = hashMapOf(
            "vname" to vName.text.toString(),
            "vemail" to vEmail.text.toString(),
            "vphone" to vPhone.text.toString(),
            "vstatus" to "Pending"
        )

        fStore.collection("events")
            .document(eventId.toString())
            .collection("volunteer")
            .document(fAuth.currentUser!!.uid)
            .set(hashMapVol)
            .addOnSuccessListener {
                    Log.d("TAG", "onSuccess: Volunteer is created")
            }
            .addOnFailureListener {
                Log.w(
                    ContentValues.TAG,
                    "Error adding document ${it.suppressedExceptions}"
                )
            }

    }

    private fun getDisplayName() {
        val docRef = fStore.collection("users").document(fAuth.currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    volunteerUID=document.id
                    volunteerName = document.getString("name")
                    binding.etVName.setText(volunteerName)

                } else {
                    Log.d("HEY", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

}