package my.edu.tarc.okuappg11.activities

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

import my.edu.tarc.okuappg11.databinding.ActivityContactUsBinding

class ContactUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val myWebView: WebView = binding.webViewContactUs
        myWebView.settings.javaScriptEnabled = true
        myWebView.loadUrl("https://ecommerce-project-dffbe.web.app/chatbot")

        supportActionBar?.title = "Chat Bot"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff262626.toInt()))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}