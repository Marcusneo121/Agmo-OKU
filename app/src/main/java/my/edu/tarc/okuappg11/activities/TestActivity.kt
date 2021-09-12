package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import my.edu.tarc.okuappg11.databinding.ActivityTestBinding

class testActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    var id: String = ""
    var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val intent: Intent = intent
        id = intent.getStringExtra("eventID").toString()
        binding.tvID.text = "Event ID : ${id}"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}