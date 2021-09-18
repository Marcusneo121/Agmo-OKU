package my.edu.tarc.okuappg11.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.okuappg11.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff000000.toInt()))

        var fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val email = binding.etForgotEmail
        var emailAddressInput: String


        binding.btnSubmit.setOnClickListener {
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
                            finish()
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