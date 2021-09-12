package my.edu.tarc.okuappg11.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.databinding.ActivityVolunteerDetailBinding

class VolunteerDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerDetailBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fStore = FirebaseFirestore.getInstance()
    }
}