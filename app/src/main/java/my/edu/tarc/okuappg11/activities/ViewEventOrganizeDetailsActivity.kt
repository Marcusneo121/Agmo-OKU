package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.databinding.ActivityViewEventOrganizeDetailsBinding
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerDetailBinding
import java.io.File

class ViewEventOrganizeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewEventOrganizeDetailsBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEventOrganizeDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "View Event Details"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        val eventId = intent.getStringExtra("EventUID")

        getImage(eventId)

        binding.btnParticipants.setOnClickListener {
            val intent = Intent(this@ViewEventOrganizeDetailsActivity, ParticipantsActivity::class.java)
            intent.putExtra("EventUID",eventId)
            startActivity(intent)
        }

        binding.btnViewEditEventDetails.setOnClickListener {
            val intent = Intent(this@ViewEventOrganizeDetailsActivity, AdminEventDetailsActivity::class.java)
            intent.putExtra("EventUID",eventId)
            startActivity(intent)
        }

        binding.btnViewVolunteerDetails.setOnClickListener(){
            val intent = Intent(this@ViewEventOrganizeDetailsActivity, VolunteerActivity::class.java)
            intent.putExtra("EventUID",eventId)
            startActivity(intent)
        }

        binding.btnPendingVolunteerRequest.setOnClickListener(){
            val intent = Intent(this@ViewEventOrganizeDetailsActivity, VolunteerRequestActivity::class.java)
            intent.putExtra("EventUID",eventId)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getImage(eventId: String?) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("EVENT_THUMBNAIL${eventId}.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        sRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.ivEventImg.setImageBitmap(bitmap)
            Log.d("CHECK", " IMAGE LOADED")
        }.addOnFailureListener{
            Log.d("CHECK", it.message.toString())
            Log.d("CHECK", "EVENT_THUMBNAIL${eventId}.jpg")

        }
    }
}