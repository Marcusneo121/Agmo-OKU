package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import my.edu.tarc.okuappg11.databinding.ActivityProfileSettingBinding

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Profile Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff262626.toInt()))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}