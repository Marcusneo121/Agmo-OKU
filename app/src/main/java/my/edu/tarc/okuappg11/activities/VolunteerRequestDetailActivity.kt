package my.edu.tarc.okuappg11.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerRegisterBinding
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerRequestDetailBinding

class VolunteerRequestDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerRequestDetailBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    private var vname:String? = null
    private var vemail:String? = null
    private var vphone:String? = null

    private lateinit var volunteerRequestArrayList: ArrayList<VolunteerRequestArrayList>
    private lateinit var volunteerRequestAdapter: VolunteerRequestAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerRequestDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val vid = intent.getStringExtra("VolunteerID")
        val eventId = intent.getStringExtra("EventUID")
        Log.d("checkEvent", eventId.toString())
        Log.d("checkVolunteer", vid.toString())

        fStore = FirebaseFirestore.getInstance()

        getData(eventId, vid)

        binding.btnAccept.setOnClickListener() {

            phoneEmail()

            val hashMapStatus = hashMapOf(
                "vstatus" to "Accepted"
            )

            fStore.collection("events")
                .document(eventId.toString())
                .collection("volunteer")
                .document(vid.toString())
                .set(hashMapStatus, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("TAG", "onSuccess: Status has updated")
                }
                .addOnFailureListener {
                    Log.w(
                        ContentValues.TAG,
                        "Error adding document ${it.suppressedExceptions}"
                    )
                }

        }

        binding.btnDecline.setOnClickListener(){

            email()
            fStore.collection("events")
                .document(eventId.toString())
                .collection("volunteer")
                .document(vid.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d("TAG", "The volunteer data is deleted.")
                }
                .addOnFailureListener {
                    Log.w(
                        ContentValues.TAG,
                        "Error adding document ${it.suppressedExceptions}"
                    )
                }

        }

        binding.btnEmail.setOnClickListener(){
            email()
        }

        binding.btnPhone.setOnClickListener(){
           phone()

        }
    }

    private fun getData(eventId: String?, vid: String?) {
            val docRef = fStore.collection("events")
                .document(eventId.toString())
                .collection("volunteer")
                .document(vid.toString())

            docRef.get()
                .addOnSuccessListener { document ->
                    //Log.d("HEYvid", vid.toString())
                    //Log.d("HEYeid", eventId.toString())

                    if (document != null) {
                        vname = document.getString("vname")
                        vemail = document.getString("vemail")
                        vphone = document.getString("vphone")

                        binding.tvRVname.text = vname
                        binding.tvREmail.text = vemail
                        binding.tvRPhone.text = vphone

                        Log.d("checkvid", vid.toString())
                        Log.d("checkeid", eventId.toString())

                    } else {
                        Log.d("HEY", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }


    }

    private fun phoneEmail(){
        var index = 0
        var selection = arrayOf("Phone Call", "Gmail")
        var option = selection[index]

        MaterialAlertDialogBuilder(this)
            .setTitle("Contact us through:")
            .setSingleChoiceItems(selection, index) { dialog, which ->
                index = which
                option = selection[which]
            }

            .setPositiveButton("OK") { dialog, which ->
                if (index == 0) {
                    val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:" + vphone)
                        //putExtra(Intent.EXTRA_PHONE_NUMBER, arrayOf(vphone.toString()))
                    }
                    startActivity(phoneIntent)
                } else {
                    val emailIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(vemail.toString()))
                    }
                    startActivity(emailIntent)

                }
            }
            .setNeutralButton("Cancel") { dialog, which ->
                Toast.makeText(this, "You can choose either phone call or email", Toast.LENGTH_SHORT).show()
            }.show()

    }

    private fun email(){
        val emailIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(vemail.toString()))
        }
        startActivity(emailIntent)
    }

    private fun phone(){
        val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:" + vphone)
            //putExtra(Intent.EXTRA_PHONE_NUMBER, arrayOf(vphone.toString()))
        }
        startActivity(phoneIntent)
    }
}