package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.databinding.ActivityViewEventOrganizeDetailsBinding
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerDetailBinding
import my.edu.tarc.okuappg11.utils.GlideLoader
import java.io.File

class ViewEventOrganizeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewEventOrganizeDetailsBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var imageURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEventOrganizeDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

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
            intent.putExtra("accessBy","eventorganizer")
            finish()
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
        fStore.collection("events").document(eventId!!).get().addOnSuccessListener {
            imageURL = it.getString("eventThumbnailURL")
            GlideLoader(this).loadUserPicture(Uri.parse(imageURL),binding.ivEventImg)
        }
    }
}