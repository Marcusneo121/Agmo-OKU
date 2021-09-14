package my.edu.tarc.okuappg11.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewStub
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.databinding.ActivityLikesBinding


class Likes : AppCompatActivity() {
    private lateinit var binding: ActivityLikesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}