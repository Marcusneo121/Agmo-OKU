package my.edu.tarc.okuappg11.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.okuappg11.databinding.ActivityForgotPassProfileBinding


class ForgotPassProfile : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPassProfileBinding
    private var emailReset : String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPassProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        var fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val email = binding.etForgotEmailProfile
        var emailAddressInput: String

        emailReset = intent.getStringExtra("emailReset").toString()

        binding.etForgotEmailProfile.clearFocus()
        binding.etForgotEmailProfile.isClickable = false
        binding.etForgotEmailProfile.isFocusable = false
        binding.etForgotEmailProfile.setText(emailReset.toString())

        binding.btnSubmitProfile.setOnClickListener {
            if (email.text.isEmpty()) {
                email.error = "Please enter email."
                return@setOnClickListener
            }

            emailAddressInput = email.text.toString()
            fAuth.sendPasswordResetEmail(emailAddressInput)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(
                            this,
                            "Email successfully sent for password reset!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val handler = Handler()
                        handler.postDelayed(object: Runnable{
                            override fun run() {
                                val preferences = getSharedPreferences("sharedLogin", Context.MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.clear()
                                editor.apply()

                                finish()
                                fAuth.signOut()
                                val i = Intent(this@ForgotPassProfile,  MainActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)
                            }
                        }, 1000)
                    } else  {
                        Toast.makeText(
                            this,
                            task.exception!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}