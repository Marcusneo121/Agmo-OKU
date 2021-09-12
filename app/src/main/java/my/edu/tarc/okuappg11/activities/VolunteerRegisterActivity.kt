package my.edu.tarc.okuappg11.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerRegisterBinding

class VolunteerRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerRegisterBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private lateinit var vName: EditText
    private lateinit var vEmail: EditText
    private lateinit var vPhone : EditText
    private var volId:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val eventId = intent.getStringExtra("EventUID")

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        vName = binding.etVName
        vEmail = binding.etVEmail
        vPhone = binding.etVPhone


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

            sendVolunteerRequest(eventId)
            Toast.makeText(this, "Your request has been recorded!", Toast.LENGTH_SHORT).show()
        }

        binding.btnVReset.setOnClickListener(){
            binding.etVName.setText("")
            binding.etVEmail.setText("")
            binding.etVPhone.setText("")
            binding.vcheckBox.isChecked = false

        }

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
            .add(hashMapVol)
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

}