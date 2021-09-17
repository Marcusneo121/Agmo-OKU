package my.edu.tarc.okuappg11.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.okuappg11.R
import my.edu.tarc.okuappg11.databinding.ActivityProfileSettingBinding
import my.edu.tarc.okuappg11.models.Constants
import my.edu.tarc.okuappg11.utils.GlideLoader

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var firestoreCheck:Boolean = false
    private var storageCheck:Boolean = false
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        supportActionBar?.title = "Profile Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(0xff262626.toInt()))

        val emailReset = binding.tvEmailProfile.text.toString()

        userID = fAuth.currentUser!!.uid

        binding.btnResetPasswordProfile.setOnClickListener {
            val intent= Intent(this@ProfileSettingActivity, ForgotPassProfile::class.java)
            intent.putExtra("emailReset", emailReset)
            startActivity(intent)
        }

        binding.imgProfile.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ){
                //Toast.makeText(this,"You already have storage permission", Toast.LENGTH_SHORT).show()
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnSaveProfileSettings.setOnClickListener {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                Constants.USER_IMAGE + userID + ".jpg")

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
            if (resultCode== Activity.RESULT_OK){

                if (data !=null){
                    try{
                        //uri of image
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.imgProfile)
                    }catch(e: Exception){
                        Toast.makeText(this, R.string.image_selection_fail, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("REQUEST CANCELLED", "Image selection cancelled")
        }

//        if(requestCode == 3){
//            if(resultCode == RESULT_OK){
//                eventLocation = data!!.getStringExtra("eventLocation")
//                latitude = data!!.getStringExtra("latitude")?.toDouble()
//                longitude = data!!.getStringExtra("longitude")?.toDouble()
//                binding.textFieldLocation.editText!!.setText(eventLocation)
//
//            }
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}