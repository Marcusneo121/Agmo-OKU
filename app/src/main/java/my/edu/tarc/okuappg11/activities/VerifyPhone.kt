package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityVerifyPhoneBinding
import java.util.concurrent.TimeUnit

class VerifyPhone : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var binding: ActivityVerifyPhoneBinding
    private var startDate:String? = null
    private var startTime:String? = null
    private var participantUID:String? =null
    private var participantName:String? = null
    private var participantEmail:String? = null
    private var eventName:String? = null
    private var userID: String? = null
    private var eventID: String? = null
    private var number: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        fAuth= FirebaseAuth.getInstance()

        val eventId = intent.getStringExtra("EventUID")
        val eventName = intent.getStringExtra("EventName")
        val startDate = intent.getStringExtra("StartDate")
        val startTime = intent.getStringExtra("StartTime")
        val participantName = intent.getStringExtra("ParticipantName")
        val participantEmail = intent.getStringExtra("ParticipantEmail")

        eventID = intent.getStringExtra("EventUID").toString()
        userID = fAuth.currentUser!!.uid
        val mobileNumber=findViewById<EditText>(R.id.phoneNumber)
        number=mobileNumber.text.toString().trim()

//        Reference
        val Login=findViewById<Button>(R.id.btnVerifyPhone)


        var currentUser = fAuth.currentUser
        /*if(currentUser != null) {
            startActivity(Intent(applicationContext, AllUpcomingEvents::class.java))
            finish()
        }*/

        Login.setOnClickListener{
            getNumber()
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, AllUpcomingEvents::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                var intent = Intent(applicationContext,VerifyOTP::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                intent.putExtra("EventUID","${eventId.toString()}")
                intent.putExtra("EventName","${eventName.toString()}")
                intent.putExtra("StartDate","${startDate.toString()}")
                intent.putExtra("StartTime","${startTime.toString()}")
                intent.putExtra("ParticipantName", "${participantName.toString()}")
                intent.putExtra("ParticipantEmail", "${participantEmail.toString()}")
                intent.putExtra("ParticipantPhoneNumber", "0${number.toString()}")
                finish()
                startActivity(intent)
            }
        }

    }

    private fun getNumber() {
        val mobileNumber=findViewById<EditText>(R.id.phoneNumber)
        number=mobileNumber.text.toString().trim()
        if(!number!!.isEmpty()){
            number="+60"+number
            sendVerificationcode (number!!)
        }else{
            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(fAuth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}