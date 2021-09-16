package my.edu.tarc.okuappg11.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.activity_about_us.*
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val imageListMarcus = ArrayList<SlideModel>()
        imageListMarcus.add(SlideModel("https://i.ibb.co/23y3rpG/marcus11.jpg", ScaleTypes.FIT))
        imageListMarcus.add(SlideModel("https://i.ibb.co/308Vhph/marcus22.jpg", ScaleTypes.FIT))
        val imageSliderMN = binding.isMarcus
        imageSliderMN.setImageList(imageListMarcus)
        btnInstaMarcus.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_ig_marcus)))
            if(intent.resolveActivity(this.packageManager!!) != null){
                startActivity(intent)
            }
        }

        val imageListKJ = ArrayList<SlideModel>()
        imageListKJ.add(SlideModel("https://i.ibb.co/6DBTb8W/KJ4.jpg", ScaleTypes.FIT))
        imageListKJ.add(SlideModel("https://i.ibb.co/mJHtydw/KJ1.jpg", ScaleTypes.FIT))
        imageListKJ.add(SlideModel("https://i.ibb.co/S6QVVCb/KJ2.jpg", ScaleTypes.FIT))
        imageListKJ.add(SlideModel("https://i.ibb.co/SxPbSVT/KJ3.jpg", ScaleTypes.FIT))
        val imageSliderKJ = binding.isKJ
        imageSliderKJ.setImageList(imageListKJ)
        btnInstaKJ.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_ig_kj)))
            if(intent.resolveActivity(this.packageManager!!) != null){
                startActivity(intent)
            }
        }

        val imageListVan = ArrayList<SlideModel>()
        imageListVan.add(SlideModel("https://i.ibb.co/HGq705V/Van2.jpg", ScaleTypes.FIT))
        imageListVan.add(SlideModel("https://i.ibb.co/d2zz6mC/Van1.jpg", ScaleTypes.FIT))
        val imageSliderVan = binding.isVan
        imageSliderVan.setImageList(imageListVan)
        btnInstaVan.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_ig_van)))
            if(intent.resolveActivity(this.packageManager!!) != null){
                startActivity(intent)
            }
        }

        val imageListLY = ArrayList<SlideModel>()
        imageListLY.add(SlideModel("https://i.ibb.co/chdrBg0/ly1.jpg", ScaleTypes.FIT))
        imageListLY.add(SlideModel("https://i.ibb.co/8PXv5tW/ly2.jpg", ScaleTypes.FIT))
        val imageSliderLY = binding.isLY
        imageSliderLY.setImageList(imageListLY)
        btnInstaLY.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_ig_ly)))
            if(intent.resolveActivity(this.packageManager!!) != null){
                startActivity(intent)
            }
        }

        binding.btnAboutSettings.setOnClickListener {
            finish()
            onBackPressed()
        }

        binding.tvContactUsEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf("tankerartgallery@gmail.com")
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.putExtra(Intent.EXTRA_SUBJECT, "To AGMO Inquiry")
            //intent.putExtra(Intent.EXTRA_TEXT, "Body of the content here...")
            intent.putExtra(Intent.EXTRA_CC, "tankerartgallery@gmail.com")
            intent.type = "text/html"
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))
        }
    }
}